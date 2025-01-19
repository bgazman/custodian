
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'



import { ThemeProvider } from './components/Main/context/ThemeContext'
import {LayoutProvider} from "./components/Main/context/LayoutContext";
const queryClient = new QueryClient()

// Add theme management
const getInitialTheme = () => {
    if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
        return 'dark'
    }
    return 'light'
}

document.documentElement.dataset.theme = getInitialTheme()
ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <ThemeProvider>
            <LayoutProvider>
                <QueryClientProvider client={queryClient}>
                    <App />
                    <ReactQueryDevtools initialIsOpen={false} />
                </QueryClientProvider>
            </LayoutProvider>
        </ThemeProvider>
    </React.StrictMode>
);

