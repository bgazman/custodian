import React, { useState, useEffect } from "react";
import EditableInfoItem from "../../components/EditableInfoItem";
import SectionHeader from "../../components/SectionHeader";
import { User } from "../../types/User";
import ErrorDisplay from "../../components/ErrorDisplay";
import { useUserDetails } from "../../hooks/useUserDetails";

const UserSecurityDetails = ({ user: initialUser }: { user: User }) => {
    const { user: fetchedUser, updateUser, loading, error } = useUserDetails(initialUser);
    const [user, setUser] = useState(fetchedUser || initialUser);
    const [editingMFA, setEditingMFA] = useState(false);
    const [editingPassword, setEditingPassword] = useState(false);
    const [editingAccess, setEditingAccess] = useState(false);

    const [formError, setFormError] = useState<string | null>(null);

    useEffect(() => {
        if (fetchedUser) setUser(fetchedUser);
    }, [fetchedUser]);




    const validateUserData = (data: any, type: string) => {
        if (type === "mfa") {
            if (data.mfaEnabled && !data.mfaMethod) {
                throw new Error("MFA method must be selected when MFA is enabled.");
            }
        }

        if (type === "password") {
            if (data.accountNonExpired === undefined) {
                throw new Error("Account expiry status is required.");
            }
        }

        if (type === "access") {
            if (!Array.isArray(data.userRoles) || data.userRoles.length === 0) {
                throw new Error("User must have at least one role assigned.");
            }

            const roleIds = new Set(data.userRoles.map((role) => role.role.id));
            if (roleIds.size !== data.userRoles.length) {
                throw new Error("Duplicate role assignments are not allowed.");
            }
        }
    };


    const handleMFAUpdate = async () => {
        try {
            validateUserData(
                {
                    mfaEnabled: user.mfaEnabled,
                    mfaMethod: user.mfaMethod,
                },
                "mfa"
            );

            const updatedUser = await updateUser({
                mfaEnabled: user.mfaEnabled,
                mfaMethod: user.mfaEnabled ? user.mfaMethod : null, // Explicitly set to null if disabled
            });

            // Sync local state with backend response
            setUser((prev) => ({
                ...prev,
                mfaEnabled: updatedUser.mfaEnabled,
                mfaMethod: updatedUser.mfaMethod, // Ensure mfaMethod reflects backend state
            }));

            setEditingMFA(false);
            setFormError(null);
        } catch (err: any) {
            setFormError(err.message || "Failed to update MFA settings.");
        }
    };





    const handlePasswordUpdate = async () => {
        try {
            validateUserData(
                { accountNonExpired: user.accountNonExpired },
                "password"
            );
            console.log("Saving password settings:", user);
            setEditingPassword(false);
            setFormError(null);
        } catch (error) {
            setFormError(error.message || "Failed to update password settings.");
        }
    };

    const handleAccessUpdate = async () => {
        try {
            validateUserData({ userRoles: user.userRoles }, "access");
            console.log("Saving access control settings:", user);
            setEditingAccess(false);
            setFormError(null);
        } catch (error) {
            setFormError(error.message || "Failed to update access control settings.");
        }
    };

    return (
        <div className="space-y-6">
            <div className="space-y-6">
                <ErrorDisplay error={formError}/>


                {/* MFA Settings */}
                <div className="bg-white shadow rounded-lg">
                    <SectionHeader
                        title="MFA Settings"
                        isEditing={editingMFA}
                        onEdit={() => setEditingMFA(true)}
                        onSave={handleMFAUpdate}
                        onCancel={() => {
                            setUser(fetchedUser || initialUser);
                            setEditingMFA(false);
                            setFormError(null);
                        }}
                        loading={loading}
                    />
                    <div className="p-6 grid grid-cols-2 gap-6">
                        <EditableInfoItem
                            label="MFA Status"
                            value={user.mfaEnabled ? "Enabled" : "Disabled"}
                            isEditing={editingMFA}
                            type="radio"
                            radioOptions={[
                                { id: "Enabled", label: "Enabled" },
                                { id: "Disabled", label: "Disabled" },
                            ]}
                            onEdit={(value) =>
                                setUser((prev) => ({
                                    ...prev,
                                    mfaEnabled: value === "Enabled",
                                    mfaMethod: value === "Enabled" ? prev.mfaMethod : null, // Reset mfaMethod when disabled
                                }))
                            }
                        />
                        <EditableInfoItem
                            label="MFA Method"
                            value={user.mfaMethod ?? ""} // Use empty string when mfaMethod is null
                            isEditing={editingMFA && user.mfaEnabled} // Editable only if both conditions are true
                            type="dropdown"
                            dropdownOptions={[
                                { id: "", label: "None" }, // Ensure only one "None" option
                                { id: "EMAIL", label: "Email" },
                                { id: "SMS", label: "SMS" },
                            ]}
                            onEdit={(value) =>
                                setUser((prev) => ({
                                    ...prev,
                                    mfaMethod: value, // Properly update mfaMethod dynamically
                                }))
                            }
                            disabled={!user.mfaEnabled} // Disable dropdown when MFA is disabled
                        />






                    </div>
                </div>
            </div>


            {/* Password & Credentials */}
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Password & Credentials"
                    isEditing={editingPassword}
                    onEdit={() => setEditingPassword(true)}
                    onSave={handlePasswordUpdate}
                    onCancel={() => {
                        setUser(initialUser);
                        setEditingPassword(false);
                        setFormError(null);
                    }}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <EditableInfoItem
                        label="Last Password Change"
                        value={
                            user.lastPasswordChange
                                ? new Date(user.lastPasswordChange).toLocaleString()
                                : "Never"
                        }
                        isEditing={false}
                    />
                    <EditableInfoItem
                        label="Account Expiry"
                        value={user.accountNonExpired ? "Active" : "Expired"}
                        isEditing={editingPassword}
                        type="radio"
                        radioOptions={[
                            {id: "Active", label: "Active"},
                            {id: "Expired", label: "Expired"},
                        ]}
                        onEdit={(value) =>
                            setUser({
                                ...user,
                                accountNonExpired: value === "Active",
                            })
                        }
                    />
                </div>
            </div>

            {/* Access Control */}
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Access Control"
                    isEditing={editingAccess}
                    onEdit={() => setEditingAccess(true)}
                    onSave={handleAccessUpdate}
                    onCancel={() => {
                        setUser(initialUser);
                        setEditingAccess(false);
                        setFormError(null);
                    }}
                />
                <div className="p-6 space-y-4">
                    <EditableInfoItem
                        label="Roles"
                        value={user.userRoles.map((role) => role.role.name).join(", ")}
                        isEditing={editingAccess}
                        onEdit={(value) =>
                            setUser({
                                ...user,
                                userRoles: value.split(",").map((roleName) => ({
                                    role: {id: roleName.trim(), name: roleName.trim()},
                                })),
                            })
                        }
                    />
                </div>
            </div>
        </div>
    );
};

export default UserSecurityDetails;
