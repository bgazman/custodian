import { ApiResponse } from './ApiResponse';
import { ApiError } from './ApiError';

export function handleApiResponse<T>(response: ApiResponse<T>): T {
    if (response.status === 'success') {
        return response.data!;
    }

    // Create an ApiError object for error responses
    const error: ApiError = {
        id: response.id,
        timestamp: response.timestamp,
        status: 'error',
        statusCode: response.statusCode,
        message: response.message,
        data: null, // Always null for error cases
        error: response.error || {
            code: 'UNKNOWN_ERROR',
            message: 'An unknown error occurred.',
        },
    };

    throw error; // Throw the structured error
}
