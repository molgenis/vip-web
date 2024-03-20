import {defineConfig} from 'vite'
import solid from 'vite-plugin-solid'

export default defineConfig({
    plugins: [solid()],
    // workaround for https://github.com/CreativeBulma/bulma-tagsinput/issues/25
    esbuild: {
        keepNames: true
    }
})
