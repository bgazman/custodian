import axios from "axios";
import { handleApiResponse} from "../utils/ApiUtils.ts";
import {createApiRequest} from "./common/RequestHandler.ts";
import {ApiClient} from "./common/ApiClient";
import {ApiResponse} from "./common/ApiResponse.ts";
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
    async login(email: string, password: string): Promise<LoginResponseData> {
        const payload = createApiRequest<LoginRequestData>({ email, password });
        console.log('Payload:', payload);

        // Raw response logging
        const response = await ApiClient.post<ApiResponse<LoginResponseData>>('/auth/login', payload);
        console.log('Raw Response:', response);

        return handleApiResponse(response);
    },
};
