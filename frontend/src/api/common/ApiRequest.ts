export interface ApiRequest<T = any> {
    id: string;
    timestamp: string;
    data: T;
}
