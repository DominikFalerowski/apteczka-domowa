import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [tailwindcss(), reactRouter()],
  resolve: {
    tsconfigPaths: true,
  },
  server: {
    // W trybie deweloperskim /api trafia do Springa pod tym samym originem — bez CORS.
    proxy: {
      "/api": "http://localhost:8080",
    },
  },
});
