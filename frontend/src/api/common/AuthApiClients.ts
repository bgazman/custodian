import axios, { AxiosInstance, AxiosRequestConfig } from "axios";

export class AuthApiClient {
    private static instance: AxiosInstance = axios.create({
        baseURL: import.meta.env.VITE_BACKEND_URL,
        headers: {
            "Content-Type": "application/json",
        },
        timeout: 10000,
    });
    // Configure Axios instance (e.g., retries)
    static configure() {
        // Disable automatic retries
        this.instance.defaults.retry = 0; // Axios does not natively support retries, but middleware might
        this.instance.defaults.timeout = 10000;
    }
    static async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.get<T>(url, config);
        return response.data;
    }


    static async post<T, U = any>(url: string, data: U, config?: AxiosRequestConfig): Promise<T> {
        try {
            console.log(`POST Request: ${url}`, data); // Log request
            const response = await this.instance.post<T>(url, data, config);
            console.log(`POST Response: ${url}`, response.data); // Log response data
            console.log('Response Headers:', response.headers); // Log response headers
            return response.data;
        } catch (error: any) {
            console.error(`POST Error: ${url}`, error.response?.data || error.message); // Log error
            throw this.handleError(error);
        }
    }


    private static handleError(error: any): any {
        if (error.response) {
            // Server responded with a non-2xx status code
            console.error('Error Status Code:', error.response.status); // Log status code
            return {
                status: 'error',
                message: error.response.data?.message || error.message,
                statusCode: error.response.status,
            };
        } else if (error.request) {
            // Request was sent but no response received
            return {
                status: 'error',
                message: 'No response received from server.',
                statusCode: 0,
            };
        } else if (error.code === 'ECONNABORTED') {
            // Timeout handling
            return {
                status: 'error',
                message: 'Request timed out. Please try again later.',
                statusCode: 0,
            };
        } else {
            // Something happened in setting up the request
            return {
                status: 'error',
                message: error.message,
                statusCode: 0,
            };
        }
    }

}
AuthApiClient.configure();