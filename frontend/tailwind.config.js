/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    darkMode: ['class', '[data-theme$="-dark"]'],
    theme: {
        extend: {
            // Layout dimensions
            height: {
                header: 'var(--header-height)',
                screen: '100vh',
            },
            width: {
                navigation: 'var(--navigation-width)',
                screen: '100vw',
            },
            maxWidth: {
                content: 'var(--content-max-width)',
                navigation: 'var(--navigation-width)',
            },
            minHeight: {
                header: 'var(--header-height)',
                content: 'calc(100vh - var(--header-height))',
            },

            // Spacing and Layout
            padding: {
                content: 'var(--content-padding)',
            },
            margin: {
                content: 'var(--content-padding)',
            },
            gap: {
                xs: 'var(--space-xs)',
                sm: 'var(--space-sm)',
                md: 'var(--space-md)',
                lg: 'var(--space-lg)',
                xl: 'var(--space-xl)',
            },

            // Colors
            colors: {
                error: 'var(--error-color)',
                success: 'var(--success-color)',
                info: 'var(--info-color)',
                warning: 'var(--warning-color)',
                border: 'var(--border-color)',
                primary: {
                    DEFAULT: 'var(--primary-color)',
                    light: 'var(--primary-light)',
                    dark: 'var(--primary-dark)',
                },
                secondary: {
                    DEFAULT: 'var(--secondary-color)',
                    light: 'var(--secondary-light)',
                    dark: 'var(--secondary-dark)',
                },
                accent: {
                    DEFAULT: 'var(--accent-color)',
                    light: 'var(--accent-light)',
                    dark: 'var(--accent-dark)',
                },
                background: {
                    DEFAULT: 'var(--background-color)',
                    alt: 'var(--background-alt)',
                    hover: 'var(--background-hover)',
                },
                text: {
                    DEFAULT: 'var(--text-color)',
                    muted: 'var(--text-muted)',
                    light: 'var(--text-light)',
                    dark: 'var(--text-dark)',
                    inverse: 'var(--text-inverse)',
                },
                surface: {
                    DEFAULT: 'var(--surface-color)',
                    hover: 'var(--surface-hover)',
                },
            },

            // Typography
            fontFamily: {
                sans: ['var(--font-family)'],
                heading: ['var(--heading-font)', 'serif'],
                body: ['var(--body-font)', 'sans-serif'],
                mono: ['var(--mono-font)', 'monospace'],
            },
            fontSize: {
                base: 'var(--font-size-base)',
                xs: 'var(--font-size-xs)',
                sm: 'var(--font-size-sm)',
                md: 'var(--font-size-md)',
                lg: 'var(--font-size-lg)',
                xl: 'var(--font-size-xl)',
            },
            lineHeight: {
                base: 'var(--line-height-base)',
                tight: 'var(--line-height-tight)',
                relaxed: 'var(--line-height-relaxed)',
            },

            // Spacing
            spacing: {
                xs: 'var(--space-xs)',
                sm: 'var(--space-sm)',
                md: 'var(--space-md)',
                lg: 'var(--space-lg)',
                xl: 'var(--space-xl)',
            },

            // Z-index
            zIndex: {
                navigation: 'var(--z-navigation)',
                modal: 'var(--z-modal)',
                popup: 'var(--z-popup)',
                tooltip: 'var(--z-tooltip)',
            },

            // Transitions
            transitionDuration: {
                DEFAULT: 'var(--transition-duration)',
            },
            transitionTimingFunction: {
                DEFAULT: 'var(--transition-timing)',
            },
        },
    },
    plugins: [],
}