export interface ApiResponse<T> {
    status: string;  // Indicates success or error
    message: string;              // Response message
    data: T | null;               // Generic payload for success
    error?: {
        code: string;             // Error code
        message: string;          // Detailed error message
        details?: Record<string, any>; // Additional error details
    }; // Error object for failure
}
