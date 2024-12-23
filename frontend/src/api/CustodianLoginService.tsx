
import {AuthApiClient} from "./common/AuthApiClients";
interface LoginRequestData {
    email: string;
    password: string;
}
export interface LoginResponseData {
    accessToken: string;
    refreshToken: string;
    email: string;
    roles: string;
}



export const CustodianLoginService = {
    /**
     * Logs in a user using email and password.
     * @param email - The user's email.
     * @param password - The user's password.
     * @returns A promise resolving to the login response data.
     */
    async login(email: string, password: string): Promise<LoginResponseData> {
        const payload: LoginRequestData = { email, password };

        // Make a POST request using ApiClient
        try {
            const response = await AuthApiClient.post<LoginResponseData>('/auth/login', payload);

            // Log the successful response
            console.log('Login successful:', response);

            return response;
        } catch (error: any) {
            // Log and re-throw the error for the caller to handle
            console.error('Login failed:', error.message || error);
            throw error;
        }
    },
};
