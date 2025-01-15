import React, { useState } from "react";
import { useGroups } from "../../hooks/useGroups.tsx";
import CreateGroupDialog from "../../components/Groups/CreateGroupDialog.tsx";
import GroupDetails from "../../components/Groups/GroupDetails.tsx";
import { Group } from "../../types/Group.ts";

const Groups: React.FC = () => {
    const { groups, loading, error, deleteGroup, toggleGroupEnabled, refetch } = useGroups();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);

    const handleViewDetails = (group: Group) => {
        setSelectedGroup(group);
    };

    const closeGroupDetails = () => {
        setSelectedGroup(null);
    };

    const handleCreateGroup = (newGroup: Group) => {
        setIsCreateDialogOpen(false);
        refetch(); // Refetch the groups list to include the new group
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
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">Manage Groups</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded"
                >
                    Create Group
                </button>
            </div>

            {/* Groups Table */}
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Members</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created At</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {groups && groups.map((group) => (
                        <tr key={group.id}>
                            <td className="px-6 py-4 whitespace-nowrap">{group.id}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{group.name}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{group.description}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{group.members?.length || 0}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                {new Date(group.createdAt).toLocaleDateString()}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => toggleGroupEnabled(group.id, group.enabled)}
                                        className={`text-sm ${
                                            group.enabled
                                                ? "text-red-500 hover:underline"
                                                : "text-green-500 hover:underline"
                                        }`}
                                    >
                                        {group.enabled ? "Disable" : "Enable"}
                                    </button>
                                    <button
                                        onClick={() => deleteGroup(group.id)}
                                        className="text-red-500 hover:underline text-sm"
                                    >
                                        Delete
                                    </button>
                                    <button
                                        onClick={() => handleViewDetails(group)}
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

            {/* Create Group Dialog */}
            <CreateGroupDialog
                open={isCreateDialogOpen}
                onClose={() => setIsCreateDialogOpen(false)}
                onGroupCreated={handleCreateGroup}
            />

            {/* Group Details Modal */}
            {selectedGroup && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                        <GroupDetails group={selectedGroup} />
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded"
                            onClick={closeGroupDetails}
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