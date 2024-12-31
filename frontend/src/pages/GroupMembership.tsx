import React, {useEffect, useState} from "react";
import { useGroupMemberships } from "../hooks/userGroupMembership";
import CreateGroupMembershipDialog from "../components/GroupMembership/CreateGroupMembershipDialog";
import GroupMembershipDetails from "../components/GroupMembership/GroupMembershipDetails";
import {GroupMembership} from "../types/GroupMembership";
import {useNavigate} from "react-router-dom";
import {Group} from "../types/Group.ts";
import CreateGroupDialog from "../components/Groups/CreateGroupDialog.tsx";


const GroupMembershipsPage: React.FC = () => {
    const navigate = useNavigate();
    const { memberships, loading, error, refetch, deleteMembership } = useGroupMemberships();
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [selectedMembership, setSelectedMembership] = useState<GroupMembership | null>(null);



    const handleViewDetails = (membership: GroupMembership) => {
        setSelectedMembership(membership); // Show details modal
    };

    const handleCloseDetails = () => {
        setSelectedMembership(null);
    };

    const handleCreateMembership = (newGroup: Group) => {
        setIsCreateDialogOpen(false);
        refetch(); // Refetch the groups list to include the new group
    };
    useEffect(() => {
        const role = sessionStorage.getItem("role");
        if (role !== "ADMIN") {
            navigate("/", { replace: true });
        }
    }, [navigate]);


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
                <h1 className="text-2xl font-bold">Manage Group Memberships</h1>
                <button
                    onClick={() => setIsCreateDialogOpen(true)}
                    className="bg-blue-500 text-white px-4 py-2 rounded"
                >
                    Add Membership
                </button>
            </div>

            {/* Memberships Table */}
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white shadow-md rounded-lg">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">User ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Group ID</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {memberships.map((membership) => (
                        <tr key={`${membership.userId}-${membership.groupId}`}>
                            <td className="px-6 py-4 whitespace-nowrap">{membership.userId}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{membership.groupId}</td>
                            <td className="px-6 py-4 whitespace-nowrap">{membership.role}</td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleViewDetails(membership)}
                                        className="text-blue-500 hover:underline text-sm"
                                    >
                                        View Details
                                    </button>
                                    <button
                                        onClick={() =>
                                            deleteMembership(membership.userId, membership.groupId)
                                        }
                                        className="text-red-500 hover:underline text-sm"
                                    >
                                        Remove
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* Add Membership Dialog */}
            <CreateGroupDialog
                open={isCreateDialogOpen}
                onClose={() => setIsCreateDialogOpen(false)} // Ensure this closes the dialog
                onGroupCreated={handleCreateMembership}
            />

            {/* Membership Details Modal */}
            {selectedMembership && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                        <GroupMembershipDetails membership={selectedMembership} />
                        <button
                            className="mt-4 bg-red-500 text-white px-4 py-2 rounded"
                            onClick={handleCloseDetails}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default GroupMembershipsPage;