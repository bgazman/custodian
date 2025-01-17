import React, { useState } from "react";
import { getAllPermissions, deletePermission as deletePermissionApi } from "../../api/generated/permission-controller/permission-controller";
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import CreatePermissionDialog from "./CreatePermissionDialog";
import PermissionDetailsDialog from "../../components/Permissions/PermissionDetailsDialog";
import { Permission } from "../../api/generated/model";

const Permissions: React.FC = () => {
    const queryClient = useQueryClient();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedPermission, setSelectedPermission] = useState<Permission | null>(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [permissionToDelete, setPermissionToDelete] = useState<Permission | null>(null);

    const { data: permissions = [], isLoading } = useQuery({
        queryKey: ['permissions'],
        queryFn: async () => {
            const response = await getAllPermissions();
            return Array.isArray(response) ? response : response.data || [];
        }
    });

    const { mutate: deletePermission, isPending: isDeleting } = useMutation({
        mutationFn: (permissionId: number) => deletePermissionApi(permissionId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['permissions'] });
            setDeleteDialogOpen(false);
            setPermissionToDelete(null);
        }
    });

    const handleDeleteClick = (permission: Permission) => {
        setPermissionToDelete(permission);
        setDeleteDialogOpen(true);
    };

    const handleDelete = () => {
        if (permissionToDelete?.id) {
            deletePermission(permissionToDelete.id);
        }
    };

    if (isLoading) return <div className="p-6">Loading...</div>;

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Permissions</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                    Create Permission
                </button>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {permissions.map((permission) => (
                        <tr key={permission.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{permission.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{permission.name}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{permission.description}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {permission.createdAt ? new Date(permission.createdAt).toLocaleDateString() : '-'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleDeleteClick(permission)}
                                        disabled={isDeleting}
                                        className="text-red-500 hover:underline text-sm disabled:opacity-50"
                                    >
                                        Delete
                                    </button>
                                    <button
                                        onClick={() => setSelectedPermission(permission)}
                                        className="text-blue-500 hover:underline text-sm"
                                    >
                                        View Details
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <CreatePermissionDialog
                open={isCreateDialogOpen}
                onClose={() => {
                    setIsCreateDialogOpen(false);
                    queryClient.invalidateQueries({ queryKey: ['permissions'] });
                }}
            />

            {deleteDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
                        <h2 className="text-xl font-semibold mb-4">Delete Permission</h2>
                        <p className="mb-6">Are you sure you want to delete {permissionToDelete?.name}? This action cannot be undone.</p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setDeleteDialogOpen(false)}
                                disabled={isDeleting}
                                className="px-4 py-2 text-gray-600 bg-gray-100 rounded hover:bg-gray-200 disabled:opacity-50"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
                                disabled={isDeleting}
                                className="px-4 py-2 text-white bg-red-500 rounded hover:bg-red-600 disabled:opacity-50"
                            >
                                {isDeleting ? 'Deleting...' : 'Delete'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {selectedPermission && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                        <PermissionDetailsDialog permission={selectedPermission} />
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                            onClick={() => setSelectedPermission(null)}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Permissions;