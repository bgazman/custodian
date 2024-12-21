import { ApiRequest } from './ApiRequest';


export function createApiRequest<T>(data: T): ApiRequest<T> {
    return {
        id: generateUniqueId(),
        timestamp: new Date().toISOString(),
        data,
    };
}

function generateUniqueId(): string {
    return crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`;
}
