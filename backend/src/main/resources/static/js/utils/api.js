/**
 * Handles POST requests and extracts response parameters (error, message).
 * @param {string} url - The endpoint URL.
 * @param {Object} data - The request body data.
 * @param {HTMLButtonElement} button - The submit button to disable during the request.
 * @param {string} [originalText='Submit'] - The original button text to restore after the request.
 * @returns {Object} - Response data and extracted URL parameters (error, message).
 */
export async function handleSubmit(url, data, button, originalText = 'Submit') {
    button.disabled = true;
    button.textContent = 'Processing...';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
            redirect: 'follow',
        });

        // Parse URL for redirect or query parameters
        const redirectUrl = new URL(response.url);
        const error = redirectUrl.searchParams.get('error');
        const message = redirectUrl.searchParams.get('message');

        if (error || message) {
            return { error, message };
        }

        // Handle JSON response (e.g., API success data)
        const contentType = response.headers.get('Content-Type');
        if (contentType && contentType.includes('application/json')) {
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'An error occurred.');
            }
            return await response.json();
        }

        // Handle non-JSON redirects
        if (response.ok) {
            return { redirectUrl: response.url };
        }

        throw new Error('Unexpected response from the server.');
    } catch (error) {
        console.error('Error in handleSubmit:', error);
        throw new Error(error.message || 'A network error occurred. Please try again.');
    } finally {
        button.disabled = false;
        button.textContent = originalText;
    }
}

/**
 * Extracts query parameters from a URL and returns them as an object.
 * @param {string} url - The URL to extract parameters from.
 * @returns {Object} - An object containing extracted parameters (error, message, etc.).
 */
function extractUrlParams(url) {
    try {
        const parsedUrl = new URL(url);
        return {
            error: parsedUrl.searchParams.get('error'),
            message: parsedUrl.searchParams.get('message'),
        };
    } catch (e) {
        console.error('Invalid URL:', url);
        return {};
    }
}
