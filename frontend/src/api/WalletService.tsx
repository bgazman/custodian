import axios from "axios";

class WalletService {
    constructor() {
        this.baseUrl = "http://localhost:3000/wallets";
    }

    // Method to login and fetch token
    async getWallets() {
        try {
            const response = await axios.get(this.baseUrl);
            if (response.status === 200) {
                return response.data; // Return the entire response data
            }
            throw new Error(response.data.message || "Login Failed");
        } catch (error) {
            console.error("Login error:", error.message);
            throw error; // Re-throw error to be handled by caller
        }
    }
}

export default new WalletService();
