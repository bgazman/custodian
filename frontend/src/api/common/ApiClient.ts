
import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

export class ApiClient {
    private static instance: AxiosInstance = axios.create({
        baseURL: import.meta.env.VITE_CUSTODIAN_BACKEND || 'http://localhost:8080/api',
        headers: {
            'Content-Type': 'application/json',
        },
        timeout: 10000, // Set a default timeout of 10 seconds
    });


    /**
     * Sends a POST request.
     * @param url - The endpoint URL.
     * @param data - The request payload.
     * @param config - Optional Axios configuration.
     * @returns The response data.
     */
    static async post<T, U = any>(url: string, data: U, config?: AxiosRequestConfig): Promise<T> {
        try {
            console.log(`POST Request: ${url}`, data); // Log request
            const response = await this.instance.post<T>(url, data, config);
            console.log(`POST Response: ${url}`, response.data); // Log response
            return response.data;
        } catch (error: any) {
            console.error(`POST Error: ${url}`, error.response?.data || error.message); // Log error
            throw this.handleError(error);
        }
    }

    /**
     * Sends a GET request.
     * @param url - The endpoint URL.
     * @param config - Optional Axios configuration.
     * @returns The response data.
     */
    static async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        try {
            console.log(`GET Request: ${url}`); // Log request
            const response = await this.instance.get<T>(url, config);
            console.log(`GET Response: ${url}`, response.data); // Log response
            return response.data;
        } catch (error: any) {
            console.error(`GET Error: ${url}`, error.response?.data || error.message); // Log error
            throw this.handleError(error);
        }
    }

    /**
     * Processes and standardizes error responses.
     * @param error - The Axios error object.
     * @returns A standardized error object.
     */
    private static handleError(error: any): any {
        if (error.response) {
            // Server responded with a non-2xx status code
            return error.response.data || {
                status: 'error',
                message: error.message,
                statusCode: error.response.status,
            };
        } else if (error.request) {
            // Request was sent but no response received
            return {
                status: 'error',
                message: 'No response received from server.',
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

