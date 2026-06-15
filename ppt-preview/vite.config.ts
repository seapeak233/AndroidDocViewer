import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: './',
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        entryFileNames: 'index-[hash].js',
        chunkFileNames: 'index-[hash].js',
        assetFileNames: 'index-[hash][extname]',
      },
    },
  },
})
