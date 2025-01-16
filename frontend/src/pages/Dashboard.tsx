import React from 'react';
import { Loader } from "lucide-react";
import { useGetAllUsers } from "../api/generated/user-controller/user-controller"; // Note the 'use' prefix

const Dashboard: React.FC = () => {
    // Use the React Query hook with proper destructuring
    const { data: users, isLoading, error, refetch } = useGetAllUsers();

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <Loader className="animate-spin h-8 w-8" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-4 text-red-500">
                {(error as Error).message}
                <button
                    onClick={() => refetch()}
                    className="ml-2 text-blue-500 hover:underline"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-6">Admin Dashboard</h1>
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    {/* ... table header ... */}
                    <tbody className="divide-y divide-gray-200">
                    {Array.isArray(users) && users.map((user) => (
                        <tr key={user.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{user.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.roles}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {new Date(user.createdAt).toLocaleDateString()}
                            </td>
                        </tr>
                    ))}

                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Dashboard;