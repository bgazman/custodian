import { getAllUsers, useDeleteUser, useUpdateUserStatus } from "../../api/generated/user-controller/user-controller";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import CreateUserDialog from "./CreateUserDialog";
import UserDetailsDialog from "./UserDetailsDialog";
import DeleteUserDialog from "./DeleteUserDialog";
import { useQuery } from '@tanstack/react-query';
import {UserBasicDTO,Secret} from "../../api/generated/model"
const UsersComponent = () => {
    const { data: users, isLoading, isError, refetch } = useQuery({
        queryKey: ['users'],
        queryFn: async () => {
            const response = await getAllUsers();
            return response;
        },
        retry: 1,
        staleTime: 30000,
        cacheTime: 60000
    });

    const { mutate: deleteUserMutation, isLoading: isDeleting } = useDeleteUser();
    const { mutate: updateStatus, isLoading: isUpdating } = useUpdateUserStatus();

    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<UserBasicDTO | null>(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [userToDelete, setUserToDelete] = useState<UserBasicDTO | null>(null);
    const navigate = useNavigate();

    const handleDelete = async () => {
        if (userToDelete) {
            deleteUserMutation({ id: userToDelete.id }, {
                onSuccess: async () => {
                    await refetch();
                    setDeleteDialogOpen(false);
                    setUserToDelete(null);
                }
            });
        }
    };

    const handleToggleEnabled = async (id: number, enabled: boolean) => {
        updateStatus({
            id,
            data: { enabled: !enabled }
        }, {
            onSuccess: () => refetch()
        });
    };

    const handleCreateUser = () => {
        refetch();
        setIsCreateDialogOpen(false);
    };

    const handleDeleteConfirm = async () => {
        await handleDelete();
    };

    const handleViewDetails = (user: UserBasicDTO) => {
        setSelectedUser(user);
    };

    const closeUserDetails = () => {
        setSelectedUser(null);
    };

    const toggleUserEnabled = async (id: number, enabled: boolean) => {
        await handleToggleEnabled(id, enabled);
    };

    const handleDeleteClick = (user: UserBasicDTO) => {
        setUserToDelete(user);
        setDeleteDialogOpen(true);
    };

    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin h-8 w-8 border-4 border-blue-500 rounded-full border-t-transparent" />
            </div>
        );
    }

    if (isError) {
        return (
            <div className="p-4 text-red-500">
                Error loading users
                <button onClick={() => refetch()} className="ml-2 text-blue-500 hover:underline">Retry</button>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Users</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded"
                >
                    Create User
                </button>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Roles</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Enabled</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {Array.isArray(users) && users.map((user) => (
                        <tr key={user.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{user.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.name}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex flex-wrap gap-1">
                                    {user.roleNames?.map((role) => (
                                        <span
                                            key={role}
                                            className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                                        >
                                               {role}
                                           </span>
                                    ))}
                                </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                   <span
                                       className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                                           user.enabled
                                               ? "bg-green-100 text-green-800"
                                               : "bg-red-100 text-red-800"
                                       }`}
                                   >
                                       {user.enabled ? "Yes" : "No"}
                                   </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {new Date(user.createdAt).toLocaleDateString()}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => toggleUserEnabled(user.id, user.enabled)}
                                        disabled={isUpdating}
                                        className={`text-sm ${
                                            user.enabled
                                                ? "text-red-500 hover:underline"
                                                : "text-green-500 hover:underline"
                                        }`}
                                    >
                                        {user.enabled ? "Disable" : "Enable"}
                                    </button>
                                    <button
                                        onClick={() => handleDeleteClick(user)}
                                        disabled={isDeleting}
                                        className="text-red-500 hover:underline text-sm"
                                    >
                                        Delete
                                    </button>
                                    <button
                                        onClick={() => handleViewDetails(user)}
                                        className="text-blue-500 hover:underline text-sm"
                                    >
                                        View Details
                                    </button>
                                    <button
                                        onClick={() => navigate(`/users/${user.id}`)}
                                        className="text-blue-500 hover:underline text-sm"
                                    >
                                        Edit
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
            <DeleteUserDialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
                onConfirm={handleDeleteConfirm}
                userName={userToDelete?.name || ''}
            />
            {selectedUser && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                        <UserDetailsDialog user={selectedUser}/>
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded"
                            onClick={closeUserDetails}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default UsersComponent;