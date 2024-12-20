import axios from "axios";
import {ApiResponse, handleApiResponse} from "../utils/ApiUtils.ts";

export const CustodianLoginService = {
    baseUrl: "http://localhost:8080/api/auth/login",

    async login(email, password) {
        try {
            const response = await axios.post(this.baseUrl, { email, password });
            return handleApiResponse(response.data);
        } catch (error) {
            console.log('Error response:', error.response?.data); // Debug log
            throw error.response?.data || {
                status: 'error',
                message: 'Network error',
                statusCode: 500
            };
        }
    }
};
