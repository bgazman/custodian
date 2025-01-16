import React, { useState, useEffect } from "react";
import EditableInfoItem from "../../components/EditableInfoItem";
import SectionHeader from "../../components/SectionHeader";
import ErrorDisplay from "../../components/ErrorDisplay";
import { getAllGroups } from '../../api/generated/group-controller/group-controller';
import { User } from "../../types/User";

const UserGroups = ({ user: initialUser }: { user: User }) => {
    const { groups: availableGroups, loading, error, refetch } = getAllGroups();

    const [user, setUser] = useState(initialUser);
    const [editingGroups, setEditingGroups] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);

    useEffect(() => {
        if (availableGroups.length && !user.groups) {
            setUser((prev) => ({
                ...prev,
                groups: availableGroups.filter((group) =>
                    prev.groups?.some((selected) => selected.id === group.id)
                ),
            }));
        }
    }, [availableGroups, user.groups]);

    const validateGroupData = (data: any) => {
        console.log("Validating group data:", data.groups);

        if (!Array.isArray(data.groups) || data.groups.length === 0) {
            throw new Error("User must belong to at least one group.");
        }

        const groupIds = new Set(data.groups.map((group) => group.id));
        if (groupIds.size !== data.groups.length) {
            throw new Error("Duplicate group assignments are not allowed.");
        }
    };

    const handleGroupsUpdate = async () => {
        try {
            validateGroupData({ groups: user.groups });

            // Update local user state (or call an API to persist changes)
            setUser((prev) => ({
                ...prev,
                groups: user.groups,
            }));

            setEditingGroups(false);
            setFormError(null);
        } catch (err: any) {
            setFormError(err.message || "Failed to update group memberships.");
        }
    };

    // Prepare dropdown options
    const dropdownOptions = availableGroups.length
        ? availableGroups.map((group) => ({ id: group.id, label: group.name }))
        : [{ id: "", label: "Loading groups..." }];

    return (
        <div className="space-y-6">
            <ErrorDisplay error={formError || error} />

            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Group Memberships"
                    isEditing={editingGroups}
                    onEdit={() => setEditingGroups(true)}
                    onSave={handleGroupsUpdate}
                    onCancel={() => {
                        setEditingGroups(false);
                        setFormError(null);
                    }}
                    loading={loading}
                />
                <div className="p-6 space-y-4">
                    <EditableInfoItem
                        label="Groups"
                        value={user.groups?.map((group) => String(group.id)) || []} // Ensure IDs are strings for consistency
                        isEditing={editingGroups}
                        type="dropdown"
                        dropdownOptions={dropdownOptions}
                        onEdit={(selectedIds) => {
                            const idsArray = Array.isArray(selectedIds) ? selectedIds : [selectedIds];
                            console.log("Selected IDs:", idsArray);

                            const selectedGroups = idsArray
                                .map((id) => availableGroups.find((group) => String(group.id) === String(id))) // Normalize ID types
                                .filter(Boolean);

                            setUser((prev) => ({
                                ...prev,
                                groups: selectedGroups, // Update user groups
                            }));
                        }}
                        multiple
                        disabled={loading}
                    />




                </div>
            </div>
        </div>
    );
};

export default UserGroups;
