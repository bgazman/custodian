import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Loader } from 'lucide-react';

const AdminPage = () => {
    // const isAdmin = localStorage.getItem('role') === 'ADMIN';
    const navigate = useNavigate();



    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-6">Admin Dashboard</h1>
            <div className="overflow-x-auto">

            </div>
        </div>
    );
};

export default AdminPage;