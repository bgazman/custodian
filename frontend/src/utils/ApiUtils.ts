// apiUtils.js
export const ApiResponse = {
    SUCCESS: 'success',
    ERROR: 'error'
};

export const handleApiResponse = (response) => {
    return {
        success: response.status === 'success',
        message: response.message,
        data: response.data,
        statusCode: response.statusCode,
        errorDetails: response.errorDetails
    };
};




