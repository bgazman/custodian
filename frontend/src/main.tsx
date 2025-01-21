
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'



import { ThemeProvider } from './components/Main/context/ThemeContext'
import {LayoutProvider} from "./components/Main/context/LayoutContext";
import DevTools from "./components/Main/devtools/DevTools.tsx";
const queryClient = new QueryClient()

// Add theme management
const getInitialTheme = () => {

    return 'default'
}


document.documentElement.dataset.theme = getInitialTheme()
ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <ThemeProvider>
            <LayoutProvider>
                <DevTools initialIsOpen={false} />

                <QueryClientProvider client={queryClient}>
                    <App />
                    {/*<ReactQueryDevtools initialIsOpen={false} />*/}
                </QueryClientProvider>
            </LayoutProvider>
        </ThemeProvider>
    </React.StrictMode>
);

