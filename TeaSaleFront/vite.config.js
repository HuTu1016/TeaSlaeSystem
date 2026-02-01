import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8089',
        changeOrigin: true
        // 不需要 rewrite，因为后端 context-path 是 /api
      },
      // 上传文件的静态资源代理
      '/uploads': {
        target: 'http://localhost:8089',
        changeOrigin: true
      }
    }
  }
})
