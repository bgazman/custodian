import React, { useState } from "react";
import { Plus, Trash } from "lucide-react";
import { useGetAllRoles } from "../../api/generated/role-controller/role-controller";
interface EditableRolesCardProps {
    label: string;
    roles: { id: string; name: string }[];
    isEditing: boolean;
    onEdit: (roles: { id: string; name: string }[]) => void;
}
const EditableRolesCard: React.FC<EditableRolesCardProps> = ({
                                                                 label,
                                                                 roles,
                                                                 isEditing,
                                                                 onEdit,
                                                             }) => {
    const { roles: availableRoles, loading: rolesLoading, error: rolesError } = useGetAllRoles();
    const [selectedRoleId, setSelectedRoleId] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [saving, setSaving] = useState(false); // Track saving state

    const handleAddRole = async () => {
        if (!selectedRoleId) {
            setError("Please select a role to add.");
            return;
        }

        const roleToAdd = availableRoles.find((role) => role.id === selectedRoleId);
        if (!roleToAdd) {
            setError("Selected role not found.");
            return;
        }
        if (roles.some((r) => r.id === selectedRoleId)) {
            setError("Role is already assigned.");
            return;
        }

        setError(null); // Clear any previous errors
        try {
            const updatedRoles = [...roles, roleToAdd];
            setSaving(true);
            await onEdit(updatedRoles); // Save roles using parent handler
            setSelectedRoleId(""); // Reset the dropdown
        } catch (err) {
            setError("Failed to add role. Please try again.");
        } finally {
            setSaving(false);
        }
    };

    const handleRemoveRole = async (roleId: string) => {
        const updatedRoles = roles.filter((r) => r.id !== roleId);

        try {
            setSaving(true);
            await onEdit(updatedRoles); // Save roles using parent handler
        } catch (err) {
            setError("Failed to remove role. Please try again.");
        } finally {
            setSaving(false);
        }
    };

    if (!isEditing) {
        return (
            <div>
                <dt className="text-sm font-medium text-gray-500">{label}</dt>
                <dd className="mt-1 flex flex-wrap gap-2">
                    {roles.map((role) => (
                        <span
                            key={role.id}
                            className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                        >
                            {role.name}
                        </span>
                    ))}
                </dd>
            </div>
        );
    }

    return (
        <div>
            <dt className="text-sm font-medium text-gray-500">{label}</dt>
            <dd className="mt-1">
                {error && (
                    <div className="mb-4 text-red-600 bg-red-100 border border-red-400 p-2 rounded">
                        {error}
                    </div>
                )}
                <div className="flex flex-wrap gap-2 mb-4">
                    {roles.map((role) => (
                        <span
                            key={role.id}
                            className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                        >
                            {role.name}
                            <button
                                onClick={() => handleRemoveRole(role.id)}
                                disabled={saving}
                                className="ml-2 text-blue-600 hover:text-blue-800"
                            >
                                <Trash className="h-4 w-4" />
                            </button>
                        </span>
                    ))}
                </div>
                <div className="flex items-center gap-2">
                    <select
                        value={selectedRoleId}
                        onChange={(e) => setSelectedRoleId(e.target.value)}
                        disabled={rolesLoading || saving}
                        className="border border-gray-300 rounded-md py-1.5 px-3 text-sm"
                    >
                        <option value="">Select a role</option>
                        {availableRoles.map((role) => (
                            <option key={role.id} value={role.id}>
                                {role.name}
                            </option>
                        ))}
                    </select>
                    <button
                        onClick={handleAddRole}
                        disabled={rolesLoading || saving}
                        className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border-2 ${
                            rolesLoading || saving
                                ? "border-gray-300 text-gray-400"
                                : "border-dashed border-gray-300 text-gray-500 hover:border-gray-400 hover:text-gray-600"
                        }`}
                    >
                        <Plus className="h-4 w-4 mr-1" />
                        Add Role
                    </button>
                </div>
                {rolesLoading && (
                    <div className="mt-2 text-gray-500">Loading available roles...</div>
                )}
                {rolesError && (
                    <div className="mt-2 text-red-600">Failed to load roles: {rolesError}</div>
                )}
            </dd>
        </div>
    );
};

export default EditableRolesCard;
