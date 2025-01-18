import React, { useState } from "react";
import { getAllSecrets, deleteSecret as deleteSecretApi } from "../../api/generated/secret-controller/secret-controller";
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import CreateSecretDialog from "./CreateSecretDialog";
import SecretDetailsDialog from "../../components/Secrets/SecretDetailsDialog";
import { Secret } from "../../api/generated/model";

const Secrets: React.FC = () => {
    const queryClient = useQueryClient();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedSecret, setSelectedSecret] = useState<Secret | null>(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [secretToDelete, setSecretToDelete] = useState<Secret | null>(null);

    const { data: secrets = [], isLoading } = useQuery({
        queryKey: ['secrets'],
        queryFn: async () => {
            const response = await getAllSecrets();
            return Array.isArray(response) ? response : response.data || [];
        }
    });

    const { mutate: deleteSecret, isPending: isDeleting } = useMutation({
        mutationFn: (secretId: number) => deleteSecretApi(secretId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['secrets'] });
            setDeleteDialogOpen(false);
            setSecretToDelete(null);
        }
    });

    const handleDeleteClick = (secret: Secret) => {
        setSecretToDelete(secret);
        setDeleteDialogOpen(true);
    };

    const handleDelete = () => {
        if (secretToDelete?.id) {
            deleteSecret(secretToDelete.id);
        }
    };

    if (isLoading) return <div className="p-6">Loading...</div>;

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Secrets</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                    Create Secret
                </button>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full bg-background shadow-md rounded-lg">
                    <thead className="bg-background">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Name</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Type</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Status</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Last Rotated</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Expires At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {secrets.map((secret) => (
                        <tr key={secret.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{secret.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{secret.name}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{secret.type}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <span className={`px-2 py-1 rounded text-xs ${
                                    secret.active ? 'bg-green-100 text-green-800' : 'bg-info/10 text-red-800'
                                }`}>
                                    {secret.active ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {secret.lastRotatedAt ? new Date(secret.lastRotatedAt).toLocaleDateString() : '-'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {secret.expiresAt ? new Date(secret.expiresAt).toLocaleDateString() : '-'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleDeleteClick(secret)}
                                        disabled={isDeleting}
                                        className="text-error hover:underline text-sm disabled:opacity-50"
                                    >
                                        Delete
                                    </button>
                                    <button
                                        onClick={() => setSelectedSecret(secret)}
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

            <CreateSecretDialog
                open={isCreateDialogOpen}
                onClose={() => {
                    setIsCreateDialogOpen(false);
                    queryClient.invalidateQueries({ queryKey: ['secrets'] });
                }}
            />

            {deleteDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-background p-6 rounded-lg shadow-lg w-full max-w-md">
                        <h2 className="text-xl font-semibold mb-4">Delete Secret</h2>
                        <p className="mb-6">Are you sure you want to delete {secretToDelete?.name}? This action cannot be undone.</p>
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setDeleteDialogOpen(false)}
                                disabled={isDeleting}
                                className="px-4 py-2 text-text bg-gray-100 rounded hover:bg-gray-200 disabled:opacity-50"
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

            {selectedSecret && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-background p-6 rounded shadow-lg w-full max-w-md">
                        <SecretDetailsDialog secret={selectedSecret} />
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                            onClick={() => setSelectedSecret(null)}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Secrets;