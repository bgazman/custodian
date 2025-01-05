import { useState } from "react";
import { ApiClient } from "../api/common/ApiClient";
import { User } from "../types/User";

interface UserUpdateData {
    name?: string;
    phoneNumber?: string;
    enabled?: boolean;
    failedLoginAttempts?: number;
}

export const useUserDetails = (initialUser: User | null) => {
    // Provide fallback if initialUser is undefined or null
    const [user, setUser] = useState<User | null>(initialUser || null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const updateUser = async (data: UserUpdateData) => {
        if (!user) {
            setError("User is not defined.");
            throw new Error("User is not defined.");
        }

        setLoading(true);
        setError(null);
        try {
            const updatedUser = await ApiClient.put<User>(
                `/api/secure/users/${user.id}`,
                data
            );
            setUser(updatedUser);
            return updatedUser;
        } catch (err: any) {
            const errorMessage = err.message || "Failed to update user.";
            setError(errorMessage);
            throw new Error(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return {
        user,
        updateUser,
        loading,
        error,
    };
};
