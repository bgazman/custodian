/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    darkMode: ['class', '[data-theme$="-dark"]'],
    theme: {
        extend: {
            height: {
                header: 'var(--header-height)'
            },
            width: {
                sidebar: 'var(--sidebar-width)'
            },
            maxWidth: {
                content: 'var(--content-max-width)'
            },
            padding: {
                content: 'var(--content-padding)'
            },
            colors: {

                error: 'var(--error-color)',
                success: 'var(--success-color)',
                info: 'var(--info-color)',
                warning: 'var(--warning-color)',
                border: 'var(--border-color)',
                primary: 'var(--primary-color)',
                secondary: 'var(--secondary-color)',
                accent: 'var(--accent-color)',
                background: 'var(--background-color)',
                text: {
                    DEFAULT: 'var(--text-color)',
                    muted: 'var(--text-muted)',
                    light: 'var(--text-light)',
                    dark: 'var(--text-dark)'
                },
            },
            fontFamily: {
                sans: ['var(--font-family)'],
                heading: ['var(--heading-font)', 'serif'],
                body: ['var(--body-font)', 'sans-serif']
            },
            fontSize: {
                base: 'var(--font-size-base)'
            },
            lineHeight: {
                base: 'var(--line-height-base)'
            },
            spacing: {
                xs: 'var(--space-xs)',
                sm: 'var(--space-sm)',
                md: 'var(--space-md)',
                lg: 'var(--space-lg)',
                xl: 'var(--space-xl)'
            }
        },
    },
    plugins: [],
}
