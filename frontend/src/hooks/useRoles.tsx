import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ApiClient } from "../api/common/ApiClient";
import { Role } from "../types/Role";

interface CreateRoleDTO {
    name: string;
    description?: string;
    parentRoleId?: number | null;
}

export const useRoles = () => {
    const [roles, setRoles] = useState<Role[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const fetchRoles = async () => {
        setLoading(true);
        setError(null);
        try {
            const fetchedRoles = await ApiClient.get<Role[]>("/api/secure/roles");
            setRoles(fetchedRoles);
        } catch (err: any) {
            console.error("Error fetching roles:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            } else {
                setError(err.message || "Failed to fetch roles.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRoles();
    }, []);

    const deleteRole = async (id: number) => {
        try {
            await ApiClient.delete(`/api/secure/roles/${id}`);
            setRoles((prevRoles) => prevRoles.filter((role) => role.id !== id));
        } catch (err: any) {
            console.error("Failed to delete role:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            }
            throw err;
        }
    };

    const updateRole = async (id: number, updates: Partial<CreateRoleDTO>) => {
        try {
            const updatedRole = await ApiClient.patch<Role>(`/api/secure/roles/${id}`, updates);
            setRoles((prevRoles) =>
                prevRoles.map((role) => (role.id === id ? { ...role, ...updatedRole } : role))
            );
            return updatedRole;
        } catch (err: any) {
            console.error("Failed to update role:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            }
            throw err;
        }
    };

    const createRole = async (roleData: CreateRoleDTO) => {
        try {
            const newRole = await ApiClient.post<Role>('/api/secure/roles', roleData);
            setRoles((prevRoles) => [...prevRoles, newRole]);
            return newRole;
        } catch (err: any) {
            console.error("Failed to create role:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            }
            throw err;
        }
    };

    const getRoleById = (id: number) => {
        return roles.find(role => role.id === id) || null;
    };

    return {
        roles,
        loading,
        error,
        refetch: fetchRoles,
        deleteRole,
        updateRole,
        createRole,
        getRoleById
    };
};