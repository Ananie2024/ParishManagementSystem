import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite' // Ensure this is the import path


export default defineConfig({
    // This tells Vite where your HTML/CSS/JS files are actually sitting
    root: 'C:/Users/User/IdeaProjects/ParishManagementSystem/src/main/resources/static',
    plugins: [
        tailwindcss(), // This replaces the need for tailwind.config.js
    ],
    server: {
        port: 5173,
        // This allows your frontend to talk to Spring Boot on 8080
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false
            }
        }
    },

    build: {
        // When you build, the output stays in the same folder
        outDir: 'C:/Users/User/IdeaProjects/ParishManagementSystem/src/main/resources/static',
        emptyOutDir: false
    }
})