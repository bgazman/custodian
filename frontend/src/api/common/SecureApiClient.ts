// secureApi.ts
import axios, { AxiosError } from 'axios';

let isRefreshing = false;
const refreshSubscribers: ((token: string) => void)[] = [];

const secureApiInstance = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
});

secureApiInstance.interceptors.request.use((config) => {
    const token = sessionStorage.getItem("access-token");
    const traceId = getTraceId();
    config.headers = {
        ...config.headers,
        'X-B3-TraceId': traceId,
        ...(token && { Authorization: `Bearer ${token}` })
    };
    return config;
});

secureApiInstance.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config;
        if (!originalRequest || error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        if (isRefreshing) {
            return new Promise<string>((resolve) => {
                refreshSubscribers.push((token) => {
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    resolve(secureApiInstance(originalRequest));
                });
            });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            const token = await refreshAccessToken();
            refreshSubscribers.forEach((callback) => callback(token));
            refreshSubscribers.length = 0;
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return secureApiInstance(originalRequest);
        } catch (error) {
            handleAuthError();
            return Promise.reject(error);
        } finally {
            isRefreshing = false;
        }
    }
);

function getTraceId(): string {
    let traceId = localStorage.getItem('traceId');
    if (!traceId) {
        traceId = crypto.randomUUID();
        localStorage.setItem('traceId', traceId);
    }
    return traceId;
}

async function refreshAccessToken(): Promise<string> {
    // Implement your token refresh logic here
    throw new Error('Not implemented');
}

function handleAuthError(): void {
    sessionStorage.clear();
    window.location.href = '/login';
}

export const customFetcher = async <T>(config: any): Promise<T> => {
    const response = await secureApiInstance(config);
    return response.data;
};

export { secureApiInstance };