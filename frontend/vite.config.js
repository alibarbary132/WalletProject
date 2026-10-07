import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// The Spring Boot API has no CORS config, so the dev server proxies /api/* to it.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
