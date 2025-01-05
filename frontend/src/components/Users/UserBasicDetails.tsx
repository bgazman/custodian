import React, { useState, useEffect, useCallback } from "react";
import EditableInfoItem from "../../components/EditableInfoItem";
import SectionHeader from "../../components/SectionHeader";
import ErrorDisplay from "../../components/ErrorDisplay";
import { useUserDetails } from "../../hooks/useUserDetails";
import { useRoles } from "../../hooks/useRoles";
import RoleInfoItem from "../../components/RoleInfoItem";

const UserBasicDetails = ({ user: initialUser }) => {
    const { user, updateUser, loading, error } = useUserDetails(initialUser);
    const { roles } = useRoles();

    const [editingBasic, setEditingBasic] = useState(false);
    const [editingAccount, setEditingAccount] = useState(false);
    const [editingRoles, setEditingRoles] = useState(false);

    const [editedUser, setEditedUser] = useState(initialUser);
    const [formError, setFormError] = useState(null);

    useEffect(() => {
        if (user) setEditedUser(user);
    }, [user]);

    const validateUserData = (data, type) => {
        if (type === "basic") {
            if (!data.name || data.name.trim() === "") throw new Error("Name cannot be empty.");
            if (data.phoneNumber && !/^\+?[1-9]\d{1,14}$/.test(data.phoneNumber)) {
                throw new Error("Invalid phone number.");
            }
        }

        if (type === "account") {
            if (
                data.failedLoginAttempts !== undefined &&
                (isNaN(data.failedLoginAttempts) || data.failedLoginAttempts < 0)
            ) {
                throw new Error("Failed login attempts must be a non-negative number.");
            }
        }

        if (type === "roles") {
            if (!Array.isArray(data.userRoles) || !data.userRoles.length) {
                throw new Error("User must have at least one role assigned.");
            }

            // Transform for backend
            const roleIds = new Set(data.userRoles.map(ur => ur.id.roleId));
            if (roleIds.size !== data.userRoles.length) {
                throw new Error("Duplicate role assignments are not allowed.");
            }
        }
    };


    const handleBasicSave = async () => {
        try {
            validateUserData({ name: editedUser.name, phoneNumber: editedUser.phoneNumber }, "basic");
            await updateUser({ name: editedUser.name, phoneNumber: editedUser.phoneNumber });
            setEditingBasic(false);
            setFormError(null);
        } catch (err) {
            setFormError(err.message || "An error occurred while saving.");
        }
    };

    const handleAccountSave = async () => {
        try {
            validateUserData({ failedLoginAttempts: editedUser.failedLoginAttempts }, "account");
            await updateUser({
                enabled: editedUser.enabled,
                failedLoginAttempts: editedUser.failedLoginAttempts,
            });
            setEditingAccount(false);
            setFormError(null);
        } catch (err) {
            setFormError(err.message || "An error occurred while saving account status.");
        }
    };

    const handleRolesSave = async () => {
        try {
            validateUserData({ userRoles: editedUser.userRoles }, "roles");

            // Convert to simple role ID set
            const roleIds = new Set(editedUser.userRoles.map(ur => ur.id.roleId));

            await updateUser({ roleIds: Array.from(roleIds) });
            setEditingRoles(false);
            setFormError(null);
        } catch (err) {
            setFormError(err.message);
        }
    };

    const handleEditName = useCallback((value) => {
        setEditedUser((prev) => ({ ...prev, name: value }));
    }, []);

    const handleEditLoginAttempts = useCallback((value) => {
        setEditedUser((prev) => ({
            ...prev,
            failedLoginAttempts: parseInt(value, 10),
        }));
    }, []);

    const handleEditRoles = useCallback((updatedRoles) => {
        setEditedUser((prev) => ({
            ...prev,
            userRoles: updatedRoles,
        }));
    }, []);

    return (
        <div className="space-y-6">
            <ErrorDisplay error={formError || error} />

            {/* Basic Information Section */}
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Basic Information"
                    isEditing={editingBasic}
                    onEdit={() => setEditingBasic(true)}
                    onSave={handleBasicSave}
                    onCancel={() => {
                        setEditedUser(user);
                        setEditingBasic(false);
                        setFormError(null);
                    }}
                    loading={loading}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <EditableInfoItem
                        label="Name"
                        value={editedUser.name}
                        isEditing={editingBasic}
                        onEdit={handleEditName}
                    />
                    <EditableInfoItem
                        label="Phone Number"
                        value={editedUser.phoneNumber || "-"}
                        isEditing={editingBasic}
                        onEdit={(value) =>
                            setEditedUser({ ...editedUser, phoneNumber: value })
                        }
                    />
                    <EditableInfoItem
                        label="Email Verified"
                        value={editedUser.emailVerified ? "Yes" : "No"}
                        isEditing={editingBasic}
                        editable={false}
                    />
                </div>
            </div>

            {/* Account Status Section */}
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Account Status"
                    isEditing={editingAccount}
                    onEdit={() => setEditingAccount(true)}
                    onSave={handleAccountSave}
                    onCancel={() => {
                        setEditedUser({
                            ...editedUser,
                            enabled: user.enabled,
                            failedLoginAttempts: user.failedLoginAttempts,
                        });
                        setEditingAccount(false);
                        setFormError(null);
                    }}
                    loading={loading}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <EditableInfoItem
                        label="Account Status"
                        value={editedUser.enabled ? "Active" : "Inactive"}
                        isEditing={editingAccount}
                        type="radio"
                        radioOptions={[
                            { id: "Active", label: "Active" },
                            { id: "Inactive", label: "Inactive" },
                        ]}
                        onEdit={(value) =>
                            setEditedUser({
                                ...editedUser,
                                enabled: value === "Active",
                            })
                        }
                    />
                    <EditableInfoItem
                        label="Login Attempts"
                        value={editedUser.failedLoginAttempts?.toString() || "0"}
                        isEditing={editingAccount}
                        type="number"
                        onEdit={handleEditLoginAttempts}
                    />
                </div>
            </div>

            {/* User Roles Section */}
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="User Roles"
                    isEditing={editingRoles}
                    onEdit={() => setEditingRoles(true)}
                    onSave={handleRolesSave}
                    onCancel={() => {
                        setEditedUser({
                            ...editedUser,
                            userRoles: user.userRoles,
                        });
                        setEditingRoles(false);
                        setFormError(null);
                    }}
                    loading={loading}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <RoleInfoItem
                        label="Roles"
                        value={editedUser.userRoles}
                        isEditing={editingRoles}
                        availableRoles={roles}
                        onEdit={handleEditRoles}
                    />

                </div>
            </div>
        </div>
    );
};

export default UserBasicDetails;
