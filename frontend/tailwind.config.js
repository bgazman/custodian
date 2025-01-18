/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    darkMode: ['class', '[data-theme="dark"]'],
    theme: {
        extend: {
            colors: {
                error: 'var(--error-color)',
                success: 'var(--success-color)',
                info: 'var(--info-color)',
                border: 'var(--border-color)',
                primary: 'var(--primary-color)',
                secondary: 'var(--secondary-color)',
                accent: 'var(--accent-color)',
                background: 'var(--background-color)',
                text: {
                    DEFAULT: 'var(--text-color)',
                    muted: 'var(--text-muted)',
                    light: 'var(--text-light)',
                    dark: 'var(--text-dark)',
                },
            },
            fontFamily: {
                sans: ['var(--font-family)', 'sans-serif'],
                heading: ['var(--heading-font)', 'serif'],
                body: ['var(--body-font)', 'serif']
            },
            fontSize: {
                base: 'var(--font-size-base)'
            },
            lineHeight: {
                base: 'var(--line-height-base)'
            },
            spacing: {
                md: 'var(--space-md)'
            }
        },
    },
    plugins: [],
}