// types/auth.types.ts
interface AuthResponse {
    accessToken: string;
    refreshToken: string;
}

interface RefreshTokenResponse {
    accessToken: string;
}

// api/client.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosError } from 'axios';

export class ApiClient {
    private static instance: AxiosInstance;
    private static isRefreshing = false;
    private static refreshSubscribers: ((token: string) => void)[] = [];

    static {
        this.instance = axios.create({
            baseURL: import.meta.env.VITE_BACKEND_URL,
            headers: {
                'Content-Type': 'application/json',
            },
            timeout: 10000,
        });
        this.setupInterceptors();
    }

    private static setupInterceptors() {
        this.instance.interceptors.request.use(
            (config) => {
                const token = sessionStorage.getItem("access-token");
                const traceId = this.getTraceId();

                config.headers = {
                    ...config.headers,
                    'X-B3-TraceId': traceId,
                };

                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }

                return config;
            },
            (error) => Promise.reject(error)
        );

        this.instance.interceptors.response.use(
            (response) => response,
            async (error: AxiosError) => {
                const originalRequest = error.config;

                if (!originalRequest) {
                    return Promise.reject(error);
                }

                // Handle 401 Unauthorized errors
                if (error.response?.status === 401 && !originalRequest._retry) {
                    if (this.isRefreshing) {
                        try {
                            const token = await new Promise<string>((resolve) => {
                                this.refreshSubscribers.push((token: string) => {
                                    resolve(token);
                                });
                            });
                            originalRequest.headers.Authorization = `Bearer ${token}`;
                            return this.instance(originalRequest);
                        } catch (err) {
                            return Promise.reject(err);
                        }
                    }

                    originalRequest._retry = true;
                    this.isRefreshing = true;

                    try {
                        const token = await this.refreshAccessToken();
                        this.refreshSubscribers.forEach((callback) => callback(token));
                        this.refreshSubscribers = [];
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        return this.instance(originalRequest);
                    } catch (refreshError) {
                        this.handleAuthError();
                        return Promise.reject(refreshError);
                    } finally {
                        this.isRefreshing = false;
                    }
                }

                return Promise.reject(error);
            }
        );
    }

    private static async refreshAccessToken(): Promise<string> {
        try {
            const refreshToken = sessionStorage.getItem('refresh-token');
            if (!refreshToken) {
                throw new Error('No refresh token available');
            }

            const response = await this.instance.post<RefreshTokenResponse>('/auth/refresh', {
                refreshToken
            });

            const { accessToken } = response.data;
            sessionStorage.setItem('access-token', accessToken);
            return accessToken;
        } catch (error) {
            this.handleAuthError();
            throw error;
        }
    }

    private static handleAuthError(): void {
        sessionStorage.clear();
        window.location.href = '/login';
    }

    private static getTraceId(): string {
        let traceId = localStorage.getItem('traceId');
        if (!traceId) {
            traceId = crypto.randomUUID();
            localStorage.setItem('traceId', traceId);
        }
        return traceId;
    }

    // API Methods
    static async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.get<T>(url, config);
        return response.data;
    }

    static async post<T, D = any>(url: string, data: D, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.post<T>(url, data, config);
        return response.data;
    }

    static async put<T, D = any>(url: string, data: D, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.put<T>(url, data, config);
        return response.data;
    }

    static async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.delete<T>(url, config);
        return response.data;
    }

    static async patch<T, D = any>(url: string, data: D, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.patch<T>(url, data, config);
        return response.data;
    }
}

