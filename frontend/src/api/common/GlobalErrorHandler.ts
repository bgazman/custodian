import { ApiError } from './ApiError';

export function handleApiError(error: ApiError): void {
    // Log the error to an external service (e.g., Sentry)
    console.error('API Error:', error);

    // Show a user-friendly message if needed
    if (error.statusCode === 401) {
        alert('Authentication failed. Please log in again.');
    } else {
        alert('An error occurred: ' + error.message);
    }
}
