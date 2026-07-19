# Quickstart: Authentication and Authorization

## Prerequisites

- Java 25 and the Maven wrapper available from the repository root
- Node.js compatible with the frontend Maven plugin (Node 22.14.0) or the supplied `ui/node`
- Configured application properties, including a stable `jwt.secret` and `jwt.expiration-ms`

## Verify the backend

1. Run focused auth and security tests:

   ```bash
   ./mvnw test -Dtest=AuthControllerTest,AuthServiceTest,JwtServiceTest
   ```

2. Start the application using the appropriate Spring profile.
3. Register a family with `POST /api/auth/register`, then log in with `POST /api/auth/login`.
4. Send the returned token in an `Authorization: Bearer <token>` header to a protected
   endpoint. Confirm missing and invalid tokens receive 401, and a foreign family ID receives
   a 403 RFC 9457 response.

## Verify the SPA

1. In `ui/`, run:

   ```bash
   npm run build
   ```

2. Start the UI with `npm run dev` and open the displayed URL.
3. Register or log in with a family slug and password. Confirm the dashboard is shown and
   authenticated requests include the bearer token.
4. Reload the page and confirm the stored token restores the authenticated session.
5. Log out and confirm the token is removed and protected routes redirect to the German login
   page.
6. Use an invalid or expired token and confirm the UI clears auth state, redirects to login,
   and presents a German error message.
