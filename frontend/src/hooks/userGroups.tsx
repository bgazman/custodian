import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {ApiClient} from "../api/common/ApiClient"; // Assuming you have an ApiClient utility
import { Group } from "../types/Group";



export const useGroups = () => {
    const [groups, setGroups] = useState<Group[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const fetchGroups = async () => {
        setLoading(true);
        setError(null);
        try {
            const fetchedGroups = await ApiClient.get<Group[]>("/api/secure/groups");
            setGroups(fetchedGroups);
        } catch (err: any) {
            console.error("Error fetching groups:", err);
            if (err.statusCode === 401) {
                navigate("/login", { replace: true });
            } else {
                setError(err.message || "Failed to fetch groups.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchGroups();
    }, []);

    // Optimistic delete
    const deleteGroup = async (id: number) => {
        try {
            await ApiClient.delete(`/groups/${id}`);
            setGroups((prevGroups) => prevGroups.filter((group) => group.id !== id));
        } catch (err) {
            console.error("Failed to delete group:", err);
            throw err;
        }
    };

    // Optimistic update (e.g., changing description, enabled state, etc.)
    const updateGroup = async (id: number, updates: Partial<Group>) => {
        try {
            const updatedGroup = await ApiClient.patch(`/groups/${id}`, updates);
            setGroups((prevGroups) =>
                prevGroups.map((group) => (group.id === id ? { ...group, ...updatedGroup } : group))
            );
        } catch (err) {
            console.error("Failed to update group:", err);
            throw err;
        }
    };

    return { groups, loading, error, refetch: fetchGroups, deleteGroup, updateGroup };
};
