import type { Config } from "@react-router/dev/config";

export default {
  // Tryb SPA: Spring serwuje statyczny build, bez renderowania po stronie serwera.
  ssr: false,
} satisfies Config;
