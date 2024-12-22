import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

// Utility to generate or fetch the traceId (UUID)
function getTraceId(): string {
    const existingTraceId = localStorage.getItem('traceId');
    if (existingTraceId) {
        return existingTraceId;
    }
    const newTraceId = crypto.randomUUID(); // Use crypto to generate UUID
    localStorage.setItem('traceId', newTraceId); // Store it for reuse
    return newTraceId;
}

export class ApiClient {
    private static instance: AxiosInstance = axios.create({
        baseURL: import.meta.env.VITE_CUSTODIAN_BACKEND || 'http://localhost:8080/api',
        headers: {
            'Content-Type': 'application/json',
        },
        timeout: 10000, // Set a default timeout of 10 seconds
    });

    // Attach an interceptor to add the traceId header
    static init() {
        this.instance.interceptors.request.use((config) => {
            const traceId = getTraceId();
            config.headers = {
                ...config.headers,
                'X-B3-TraceId': traceId, // Include the traceId in every request
            };
            console.log(`Request TraceId: ${traceId}`);
            return config;
        });
    }

    /**
     * Set a custom header to be used in all requests.
     * @param key - The header name.
     * @param value - The header value.
     */
    static setHeader(key: string, value: string): void {
        this.instance.defaults.headers.common[key] = value;
    }

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
            console.log(`POST Response: ${url}`, response.data); // Log response data
            console.log('Response Headers:', response.headers); // Log response headers
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
            console.log(`GET Response: ${url}`, response.data); // Log response data
            console.log('Response Headers:', response.headers); // Log response headers
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

// Initialize the interceptor
ApiClient.init();
