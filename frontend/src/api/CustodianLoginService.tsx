
import {AuthApiClient} from "./common/AuthApiClients";
interface LoginRequestData {
    clientId: string;
    email: string;
    password: string;
}

export interface LoginResponseData {
    accessToken: string;
    refreshToken: string;
}

export const CustodianLoginService = {
    /**
     * Logs in a user using client ID, email, and password.
     * @param clientId - The client identifier (e.g., 'iam-dashboard').
     * @param email - The user's email.
     * @param password - The user's password.
     * @returns A promise resolving to the login response data.
     */
    async login(clientId: string, email: string, password: string): Promise<LoginResponseData> {
        const payload: LoginRequestData = { clientId, email, password };

        try {
            // Make a POST request using AuthApiClient
            const response = await AuthApiClient.post<LoginResponseData>('/auth/login', payload);

            // Log the successful response
            console.log('Login successful:', response);

            return response;
        } catch (error: any) {
            // Check for Axios-specific error structure
            if (error.response) {
                console.error('Login failed with response error:', error.response.data);
                throw new Error(error.response.data.message || 'Login failed.');
            } else if (error.request) {
                console.error('Login failed with no response from server:', error.request);
                throw new Error('No response from server. Please try again later.');
            } else {
                console.error('Login failed with unexpected error:', error.message);
                throw new Error(error.message || 'Unexpected error occurred.');
            }
        }
    },
};
