import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import { useUsers } from "../hooks/userUsers";
import CreateUserDialog from "../components/Users/CreateUserDialog";

const UsersPage = () => {
    const navigate = useNavigate();
    const { users, loading, error, deleteUser, toggleUserEnabled, refetch } = useUsers();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    useEffect(() => {
        const isAdmin = localStorage.getItem("role") === "ADMIN";
        if (!isAdmin) {
            navigate("/", { replace: true });
        }
    }, [navigate]);
    const handleCreateUser = (newUser) => {
        refetch(); // Refetch the users list to include the new user
    };
    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin h-8 w-8 border-4 border-blue-500 rounded-full border-t-transparent"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-4 text-red-500">
                {error}
                <button
                    onClick={refetch}
                    className="ml-2 text-blue-500 hover:underline"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Users</h1>
                <button
                    variant="contained"
                    color="primary"
                    onClick={() => setIsCreateDialogOpen(true)}
                >
                    Create User
                </button>
            </div>
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Enabled</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{user.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.role}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {user.enabled ? "Yes" : "No"}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {new Date(user.createdAt).toLocaleDateString()}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() =>
                                            toggleUserEnabled(user.id, user.enabled)
                                        }
                                        className={`text-sm ${
                                            user.enabled
                                                ? "text-red-500 hover:underline"
                                                : "text-green-500 hover:underline"
                                        }`}
                                    >
                                        {user.enabled ? "Disable" : "Enable"}
                                    </button>
                                    <button
                                        onClick={() => deleteUser(user.id)}
                                        className="text-red-500 hover:underline text-sm"
                                    >
                                        Delete
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <CreateUserDialog
                open={isCreateDialogOpen}
                onClose={() => setIsCreateDialogOpen(false)}
                onUserCreated={handleCreateUser}
            />
        </div>
    );
};

export default UsersPage;
