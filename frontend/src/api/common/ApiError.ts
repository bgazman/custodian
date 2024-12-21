export interface ApiError {
    id: string; // Unique identifier for the error (can match the request ID)
    timestamp: number; // Timestamp when the error occurred
    status: 'error'; // Explicitly defined as 'error' for type safety
    statusCode: number; // HTTP status code (e.g., 400, 401, 500)
    message: string; // High-level error message
    data: null; // No data is expected in error responses
    error: {
        code: string; // Specific error code for categorization (e.g., "UNAUTHORIZED", "VALIDATION_FAILED")
        message: string; // Detailed error message
        details?: Record<string, any>; // Optional additional details for debugging
    };
}
