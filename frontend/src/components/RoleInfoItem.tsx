import {useState} from "react";

interface UserRole {
    id: {
        userId: number;
        roleId: number;
    };
    role: {
        id: number;
        name: string;
        description: string;
    };
}

interface Role {
    id: number;
    name: string;
    description: string;
}

interface RoleInfoItemProps {
    label: string;
    value: UserRole[];
    isEditing: boolean;
    onEdit: (value: UserRole[]) => void;
    availableRoles: Role[];
}

const RoleInfoItem: React.FC<RoleInfoItemProps> = ({
                                                       label,
                                                       value,
                                                       isEditing,
                                                       onEdit,
                                                       availableRoles
                                                   }) => {
    const [selectedRoleId, setSelectedRoleId] = useState("");
    const existingRoleIds = new Set(value.map(r => r.role.id));

    const handleRemoveRole = (roleId: number) => {
        onEdit(value.filter(r => r.role.id !== roleId));
    };

    const handleAddRole = (roleId: string) => {
        if (!roleId) return;
        const roleToAdd = availableRoles.find(role => role.id === Number(roleId));
        if (!roleToAdd || existingRoleIds.has(roleToAdd.id)) return;

        const newUserRole: UserRole = {
            id: {
                userId: value[0]?.id.userId || 0, // Get existing userId or default
                roleId: roleToAdd.id
            },
            role: roleToAdd,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        };

        onEdit([...value, newUserRole]);
        setSelectedRoleId("");
    };

    if (!isEditing) {
        return (
            <div>
                <dt className="text-sm font-medium text-text-muted">{label}</dt>
                <dd className="mt-1 flex flex-wrap gap-2">
                    {value.map((userRole) => (
                        <span
                            key={`${userRole.id.userId}-${userRole.id.roleId}`}
                            className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-info/10 text-blue-800"
                        >
                           {userRole.role.name}
                       </span>
                    ))}
                </dd>
            </div>
        );
    }

    return (
        <div>
            <dt className="text-sm font-medium text-text-muted">{label}</dt>
            <dd className="mt-1">
                <div className="flex flex-wrap gap-2 mb-4">
                    {value.map((userRole) => (
                        <span
                            key={`${userRole.id.userId}-${userRole.id.roleId}`}
                            className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-info/10 text-blue-800"
                        >
                           {userRole.role.name}
                            <button
                                onClick={() => handleRemoveRole(userRole.role.id)}
                                className="ml-2 text-blue-600 hover:text-blue-800"
                            >
                               Remove
                           </button>
                       </span>
                    ))}
                </div>

                <div className="flex items-center gap-2">
                    <select
                        className="border border-border rounded-md py-1.5 px-3 text-sm"
                        value={selectedRoleId}
                        onChange={(e) => handleAddRole(e.target.value)}
                    >
                        <option value="">Select a role</option>
                        {availableRoles
                            .filter(role => !existingRoleIds.has(role.id))
                            .map((role) => (
                                <option key={role.id} value={role.id}>
                                    {role.name}
                                </option>
                            ))}
                    </select>
                </div>
            </dd>
        </div>
    );
};

export default RoleInfoItem;