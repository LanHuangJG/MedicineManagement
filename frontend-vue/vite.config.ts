import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue({
        template: {
            compilerOptions: {
                // 所有以 mdui- 开头的标签名都是 mdui 组件
                isCustomElement: (tag) => tag.startsWith('mdui-')
            }
        }
    }), VueDevTools(), AutoImport({
        resolvers: [ElementPlusResolver()],
    }),
        Components({
            resolvers: [ElementPlusResolver()],
        }),],
    server: {
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, ""),
            },
        },
    },
})
