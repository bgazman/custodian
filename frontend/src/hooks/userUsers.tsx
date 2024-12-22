import { useState, useEffect } from 'react';
import axios from 'axios';
import { User } from '../types/User';
import {useNavigate} from "react-router-dom";
import {ApiResponse} from "../api/common/ApiResponse.ts";
import {ApiClient} from "../api/common/ApiClient.ts";

export const useUsers = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('access-token');
        if (!token) {
            navigate('/login', { replace: true });
            return;
        }
    }, [navigate]);
    const fetchUsers = async () => {
        try {
            setLoading(true); // Start loading

            const token = localStorage.getItem('access-token');
            if (!token) {
                setError('User is not authenticated');
                return;
            }

            // Use ApiClient to make the GET request with Authorization header
            const users: User[] = await ApiClient.get<User[]>('/users', {
                headers: { Authorization: `Bearer ${token}` },
            });

            // Update users state directly
            setUsers(users);
        } catch (err: any) {
            console.error('Error fetching users:', err);
            setError(err.message || 'Failed to fetch users');
        } finally {
            setLoading(false); // End loading
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    return { users, loading, error, refetch: fetchUsers };
};
