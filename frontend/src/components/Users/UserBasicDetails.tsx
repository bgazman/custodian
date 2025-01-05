import React, { useState } from 'react';
import { Pencil, X, Check } from 'lucide-react';
import EditableRolesCard from "../../components/Users/EditableRoleCard.tsx";
import {useUserDetails} from "../../hooks/useUserDetails.tsx";

const EditableInfoItem = ({ label, value, isEditing, onEdit, editable = true }) => {
    if (!editable || !isEditing) {
        return (
            <div>
                <dt className="text-sm font-medium text-gray-500">{label}</dt>
                <dd className="mt-1 text-sm text-gray-900">{value}</dd>
            </div>
        );
    }

    return (
        <div>
            <dt className="text-sm font-medium text-gray-500">{label}</dt>
            <input
                className="mt-1 block w-full rounded-md border border-gray-300 py-1.5 px-3 text-gray-900 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                value={value}
                onChange={(e) => onEdit(e.target.value)}
            />
        </div>
    );
};

const SectionHeader = ({ title, isEditing, onEdit, onSave, onCancel }) => (
    <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h2 className="text-xl font-semibold">{title}</h2>
        <div>
            {isEditing ? (
                <div className="space-x-2">
                    <button onClick={onCancel} className="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                        <X className="h-4 w-4 mr-1" />
                        Cancel
                    </button>
                    <button onClick={onSave} className="inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
                        <Check className="h-4 w-4 mr-1" />
                        Save
                    </button>
                </div>
            ) : (
                <button onClick={onEdit} className="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                    <Pencil className="h-4 w-4 mr-1" />
                    Edit
                </button>
            )}
        </div>
    </div>
);

const UserBasicDetails = ({ user: initialUser }) => {
    const { user, updateUser, loading } = useUserDetails(initialUser);
    const [editingBasic, setEditingBasic] = useState(false);
    const [editingAccount, setEditingAccount] = useState(false);
    const [editedUser, setEditedUser] = useState(user);

    const handleBasicSave = async () => {
        try {
            await updateUser({
                name: editedUser.name,
                phoneNumber: editedUser.phoneNumber
            });
            setEditingBasic(false);
        } catch (error) {
            console.error('Failed to update user:', error);
        }
    };

    const handleAccountSave = async () => {
        try {
            await updateUser({
                enabled: editedUser.enabled
            });
            setEditingAccount(false);
        } catch (error) {
            console.error('Failed to update user:', error);
        }
    };

    return (
        <div className="space-y-6">
            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Basic Information"
                    isEditing={editingBasic}
                    onEdit={() => setEditingBasic(true)}
                    onSave={handleBasicSave}
                    onCancel={() => {
                        setEditedUser({...editedUser, name: user.name, phoneNumber: user.phoneNumber});
                        setEditingBasic(false);
                    }}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <EditableInfoItem
                        label="Name"
                        value={editedUser.name}
                        isEditing={editingBasic}
                        onEdit={(value) => setEditedUser({...editedUser, name: value})}
                    />
                    <EditableInfoItem
                        label="Email"
                        value={editedUser.email}
                        isEditing={editingBasic}
                        editable={false}
                    />
                    <EditableInfoItem
                        label="Phone Number"
                        value={editedUser.phoneNumber || '-'}
                        isEditing={editingBasic}
                        onEdit={(value) => setEditedUser({...editedUser, phoneNumber: value})}
                    />
                    <EditableInfoItem
                        label="Email Verified"
                        value={editedUser.emailVerified ? 'Yes' : 'No'}
                        isEditing={editingBasic}
                        editable={false}
                    />
                </div>
            </div>

            <div className="bg-white shadow rounded-lg">
                <SectionHeader
                    title="Account Status"
                    isEditing={editingAccount}
                    onEdit={() => setEditingAccount(true)}
                    onSave={handleAccountSave}
                    onCancel={() => {
                        setEditedUser({...editedUser, enabled: user.enabled});
                        setEditingAccount(false);
                    }}
                />
                <div className="p-6 grid grid-cols-2 gap-6">
                    <EditableInfoItem
                        label="Account Status"
                        value={editedUser.enabled ? 'Active' : 'Inactive'}
                        isEditing={editingAccount}
                        onEdit={(value) => setEditedUser({...editedUser, enabled: value === 'Active'})}
                    />
                    <EditableInfoItem
                        label="Account Locked"
                        value={editedUser.accountNonLocked ? 'No' : 'Yes'}
                        isEditing={editingAccount}
                        editable={false}
                    />
                    <EditableInfoItem
                        label="Login Attempts"
                        value={editedUser.failedLoginAttempts?.toString() || '0'}
                        isEditing={editingAccount}
                        editable={false}
                    />
                    <EditableInfoItem
                        label="Last Login"
                        value={editedUser.lastLoginTime ? new Date(editedUser.lastLoginTime).toLocaleString() : 'Never'}
                        isEditing={editingAccount}
                        editable={false}
                    />
                </div>
            </div>

            <EditableRolesCard roles={user.userRoles} />
        </div>
    );
};

export default UserBasicDetails;