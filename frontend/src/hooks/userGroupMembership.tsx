import { useState, useEffect } from "react";
import { ApiClient } from "../api/common/ApiClient";
import {GroupMembership} from "../types/GroupMembership";


export const useGroupMemberships = () => {
    const [memberships, setMemberships] = useState<GroupMembership[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const fetchMemberships = async () => {
        console.log("Fetching memberships with fetch...");
        setLoading(true);
        setError(null);
        try {
            const response = await fetch("https://api.example.com/group_memberships");
            if (!response.ok) throw new Error("Failed to fetch");
            const data = await response.json();
            console.log("Fetched memberships with fetch:", data);
            setMemberships(data);
        } catch (err) {
            console.error("Error fetching memberships with fetch:", err);
            setError("Failed to fetch group memberships.");
        } finally {
            setLoading(false);
        }
    };

    const createMembership = async (membership: GroupMembership) => {
        try {
            const response = await ApiClient.post("/group_memberships", membership);
            setMemberships((prev) => [...prev, response]);
        } catch (err) {
            console.error("Error creating membership:", err);
            throw err;
        }
    };

    const deleteMembership = async (userId: number, groupId: number) => {
        try {
            await ApiClient.delete(`/group_memberships?userId=${userId}&groupId=${groupId}`);
            setMemberships((prev) =>
                prev.filter((m) => m.userId !== userId || m.groupId !== groupId)
            );
        } catch (err) {
            console.error("Error deleting membership:", err);
            throw err;
        }
    };

    useEffect(() => {
        fetchMemberships();
    }, []);

    return { memberships, loading, error, refetch: fetchMemberships, createMembership, deleteMembership };
};
