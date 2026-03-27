# ─── Stage 1: Build UI ────────────────────────────────────────────────────────
FROM node:22-alpine AS ui-build

WORKDIR /app/ui

COPY ui/package.json ui/package-lock.json ./
RUN npm ci

COPY ui/ ./
# Build output goes to ../src/main/resources/static (configured in vite.config.ts)
# but inside Docker we redirect it to a location Stage 2 can copy from
RUN npx vite build --outDir /app/static --emptyOutDir

# ─── Stage 2: Build Java application ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-25 AS app-build

WORKDIR /app

# Cache dependencies before copying source (layer cache optimisation)
COPY pom.xml ./
RUN mvn dependency:go-offline -q

COPY src/ src/
# Copy UI assets built in Stage 1 into the Spring Boot static resources folder
COPY --from=ui-build /app/static src/main/resources/static/

# skip-ui profile skips frontend-maven-plugin (UI already built above)
RUN mvn package -Pskip-ui -DskipTests -q

# ─── Stage 3: Runtime image ───────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine AS runtime

# Run as a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=app-build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
