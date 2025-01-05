import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ApiClient } from "../api/common/ApiClient";
import { User } from "../types/User";

export const useUsers = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const fetchUsers = async () => {
        setLoading(true);
        setError(null);
        try {
            const fetchedUsers = await ApiClient.get<User[]>("/api/secure/users");
            setUsers(fetchedUsers);
        } catch (err: any) {
            console.error("Error fetching users:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            } else {
                setError(err.message || "Failed to fetch users.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    // Optimistic delete
    const deleteUser = async (id: number) => {
        try {
            await ApiClient.delete(`/api/secure/users/${id}`);
            setUsers((prevUsers) => prevUsers.filter((user) => user.id !== id));
        } catch (err) {
            console.error("Failed to delete user:", err);
            throw err;
        }
    };

    // Optimistic toggle
    const toggleUserEnabled = async (id: number, enabled: boolean) => {
        try {
            const endpoint = enabled ? `/api/secure/users/disable/${id}` : `/api/secure/users/enable/${id}`;
            await ApiClient.post(endpoint, {});
            setUsers((prevUsers) =>
                prevUsers.map((user) =>
                    user.id === id ? { ...user, enabled: !enabled } : user
                )
            );
        } catch (err) {
            console.error(`Failed to ${enabled ? "disable" : "enable"} user:`, err);
            throw err;
        }
    };
    interface CreateUserRequest {
        name: string;
        email: string;
        password: string;
        enabled?: boolean;
        mfaEnabled?: boolean;
        phoneNumber?: string;
        roleIds: number[]; // Send role IDs instead of userRoles
    }

    const createUser = async (userData: Partial<User>) => {
        try {
            // Transform `userRoles` to `roleIds`
            const requestData: CreateUserRequest = {
                name: userData.name || '',
                email: userData.email || '',
                password: userData.password || '',
                enabled: userData.enabled || false,
                mfaEnabled: userData.mfaEnabled || false,
                phoneNumber: userData.phoneNumber || '',
                roleIds: userData.userRoles
                    ? userData.userRoles.map((role) => role.role.id)
                    : [], // Extract role IDs
            };

            // Make the API call
            const newUser = await ApiClient.post<User>('/api/secure/users', requestData);

            // Update local state with the new user
            setUsers((prev) => [...prev, newUser]);

            return newUser;
        } catch (err) {
            console.error('Failed to create user:', err);
            throw err;
        }
    };
    return { users, loading, error, refetch: fetchUsers, deleteUser, toggleUserEnabled,createUser };
};
