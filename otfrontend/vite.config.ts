import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,      // 指定 port 為 5173
    strictPort: true // 如果 5173 被佔用，直接報錯退出 (不會自動跳 5174)
  }
})