

import {ApiResponse} from "@/api/common/ApiResponse.ts";

export function handleApiResponse<T>(response: ApiResponse<T>, rawResponse?: any): T {
    // Log the headers if the raw response is provided
    if (rawResponse) {
        console.log('Response Headers:', rawResponse.headers);
    }

    if (response.status === 'success') {
        console.log('Response Headers:', rawResponse.headers);

        return response.data!; // Ensure the correct type is returned
    }

    throw {
        status: 'error',
        message: response.message,
        error: response.error,
    };
}






