export interface ApiResponse<T> {
    id: string;                   // Unique identifier
    timestamp: number;            // Timestamp of the response
    status: 'success' | 'error';  // Indicates success or error
    statusCode: number;           // HTTP status code
    message: string;              // Response message
    data: T | null;               // Generic payload for success
    error?: {
        code: string;             // Error code
        message: string;          // Detailed error message
        details?: Record<string, any>; // Additional error details
    }; // Error object for failure
}
