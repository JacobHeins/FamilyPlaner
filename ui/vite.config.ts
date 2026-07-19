import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: "../src/main/resources/static",
    emptyOutDir: true,
  },
  server: {
    host: true,
    port: 5173,
    proxy: {
      "/api": {
        target: "http://host.docker.internal:8080",
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on("proxyReq", (_proxyReq, req) => {
            console.log(`[api-proxy] -> ${req.method} ${req.url}`);
          });
          proxy.on("proxyRes", (proxyRes, req) => {
            console.log(
              `[api-proxy] <- ${proxyRes.statusCode} ${req.method} ${req.url}`,
            );
          });
          proxy.on("error", (err, req) => {
            console.error(
              `[api-proxy] error for ${req.method} ${req.url}: ${err.message}`,
            );
          });
        },
      },
    },
  },
});
