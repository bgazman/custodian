import { useState, useEffect } from 'react';
import axios from 'axios';
import { User } from '../types/User';
import {useNavigate} from "react-router-dom";

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
            const token = localStorage.getItem('access-token');
            const response = await axios.get('http://localhost:8080/api/users', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(response.data);
        } catch (err) {
            setError('Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    return { users, loading, error, refetch: fetchUsers };
};
