

import {ApiResponse} from "@/api/common/ApiResponse.ts";

export function handleApiResponse<T>(response: ApiResponse<T>): T {
    if (response.status === 'success') {
        return response.data!; // Ensure the correct type is returned
    }

    throw {
        id: response.id,
        timestamp: response.timestamp,
        status: 'error',
        statusCode: response.statusCode,
        message: response.message,
        error: response.error,
    };
}





