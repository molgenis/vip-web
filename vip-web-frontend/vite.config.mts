import { defineConfig } from "vite";
import solid from "vite-plugin-solid";

export default defineConfig(({ command }) => ({
  plugins: [solid()],
  esbuild: {
    keepNames: true, // workaround for https://github.com/CreativeBulma/bulma-tagsinput/issues/25
    pure: command === "build" ? ["console.log"] : [],
  },
  build: {
    // unclear how to migrate to new default Lightning CSS
    cssMinify: "esbuild",
    // unclear how to migrate to new default Oxc e.g. esbuild.charset and esbuild.pure
    minify: "esbuild",
  },
}));
