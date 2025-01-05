import { useState } from "react";
import { ApiClient } from "../api/common/ApiClient";
import { User } from "../types/User";

interface UserUpdateData {
    name?: string;
    phoneNumber?: string;
    enabled?: boolean;
}

export const useUserDetails = (initialUser: User) => {
    const [user, setUser] = useState<User>(initialUser);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const updateUser = async (data: UserUpdateData) => {
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
            const errorMessage = err.message || "Failed to update user";
            setError(errorMessage);
            throw new Error(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return {
        user,
        loading,
        error,
        updateUser
    };
};