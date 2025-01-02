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
            await ApiClient.delete(`/users/${id}`);
            setUsers((prevUsers) => prevUsers.filter((user) => user.id !== id));
        } catch (err) {
            console.error("Failed to delete user:", err);
            throw err;
        }
    };

    // Optimistic toggle
    const toggleUserEnabled = async (id: number, enabled: boolean) => {
        try {
            const endpoint = enabled ? `/users/disable/${id}` : `/users/enable/${id}`;
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

    return { users, loading, error, refetch: fetchUsers, deleteUser, toggleUserEnabled };
};
