import React, { useState } from "react";
import { getAllGroups, deleteGroup as deleteGroupApi, updateGroup } from "../../api/generated/group-controller/group-controller";
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import CreateGroupDialog from "../../components/Groups/CreateGroupDialog";
import GroupDetailsDialog from "./GroupDetailsDialog";
import { GroupDTO as Group} from "../../api/generated/model";

const Groups: React.FC = () => {
    const queryClient = useQueryClient();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [groupToDelete, setGroupToDelete] = useState<Group | null>(null);

    const { data: groups = [], isLoading, isError, error, refetch } = useQuery({
        queryKey: ['groups'],
        queryFn: async () => {
            const response = await getAllGroups();
            return Array.isArray(response) ? response : response.data || [];
        }
    });

    const { mutate: deleteGroup, isPending: isDeleting } = useMutation({
        mutationFn: (groupId: number) => deleteGroupApi(groupId),
        onSuccess: async () => {
            await refetch();
            setDeleteDialogOpen(false);
            setGroupToDelete(null);
        }
    });

    const handleDeleteClick = (group: Group) => {
        setGroupToDelete(group);
        setDeleteDialogOpen(true);
    };

    const handleDelete = () => {
        if (groupToDelete) {
            deleteGroup(groupToDelete.id);
        }
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Groups</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded"
                >
                    Create Group
                </button>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full bg-background shadow-md rounded-lg">
                    <thead className="bg-background">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Name</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Description</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Parent Group</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Created At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-text-muted uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {groups.map((group) => (
                        <tr key={group.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{group.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{group.name}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{group.description}</td>
                            <td className="px-6 py-4 whitespace-nowrap">-</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {group.createdAt ? new Date(group.createdAt).toLocaleDateString() : '-'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleDeleteClick(group)}
                                        disabled={isDeleting}
                                        className="text-error hover:underline text-sm disabled:opacity-50"
                                    >
                                        Delete
                                    </button>
                                    <button
                                        onClick={() => setSelectedGroup(group)}
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

            <CreateGroupDialog
                open={isCreateDialogOpen}
                onClose={() => {
                    setIsCreateDialogOpen(false);
                    refetch();
                }}
            />

            {deleteDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-background p-6 rounded-lg shadow-lg w-full max-w-md">
                        <h2 className="text-xl font-semibold mb-4">Delete Group</h2>
                        <p className="mb-6">Are you sure you want to delete {groupToDelete?.name}? This action cannot be undone.</p>
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

            {selectedGroup && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-background p-6 rounded shadow-lg w-full max-w-md">
                        <GroupDetailsDialog group={selectedGroup} />
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded"
                            onClick={() => setSelectedGroup(null)}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Groups;