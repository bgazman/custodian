import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader } from 'lucide-react';

const AdminPage = () => {
    // const isAdmin = localStorage.getItem('role') === 'ADMIN';
    const navigate = useNavigate();



    return (
        <div className="p-6">
            <h1>Content</h1>

        </div>
    );
};

export default AdminPage;