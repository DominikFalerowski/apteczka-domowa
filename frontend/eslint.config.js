import js from "@eslint/js";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import reactHooks from "eslint-plugin-react-hooks";
import { defineConfig, globalIgnores } from "eslint/config";
import globals from "globals";
import tseslint from "typescript-eslint";

// ESLint odpowiada wyłącznie za jakość kodu; wygląd należy do Prettiera.
export default defineConfig([
  globalIgnores(["build/", ".react-router/", "node/"]),
  js.configs.recommended,
  tseslint.configs.recommended,
  reactHooks.configs.flat.recommended,
  {
    languageOptions: {
      globals: globals.browser,
    },
  },
  // Musi być ostatni: wyłącza reguły kolidujące z formatowaniem Prettiera.
  eslintConfigPrettier,
]);
