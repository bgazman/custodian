import axios, { AxiosInstance, AxiosRequestConfig } from "axios";

export class AuthApiClient {
    private static instance: AxiosInstance = axios.create({
        baseURL: import.meta.env.VITE_CUSTODIAN_BACKEND || "http://localhost:8080/api",
        headers: {
            "Content-Type": "application/json",
        },
        timeout: 10000,
    });

    static async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.get<T>(url, config);
        return response.data;
    }

    static async post<T, U = any>(url: string, data: U, config?: AxiosRequestConfig): Promise<T> {
        const response = await this.instance.post<T>(url, data, config);
        return response.data;
    }
}
