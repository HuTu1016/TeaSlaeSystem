import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    server: {
        port: 5174, // 使用不同于管理端的端口，避免冲突
        proxy: {
            '/api': {
                target: 'http://localhost:8089',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '/api') // 根据实际后端路由调整，如果后端就是 /api 开头则不需要 rewrite 去掉
            }
        }
    }
})
