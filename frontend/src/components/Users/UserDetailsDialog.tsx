import React, { useState } from "react";
import { UserCircle } from "lucide-react";

interface UserDetailsProps {
    user: {
        id?: number;
        email: string;
        enabled: boolean;
        emailVerified?: boolean;
        accountNonExpired?: boolean;
        accountNonLocked?: boolean;
        credentialsNonExpired?: boolean;
        role: string;
        failedLoginAttempts?: number;
        lockedUntil?: string | null;
        lastLoginTime?: string | null;
        lastPasswordChange?: string | null;
        createdAt?: string;
        updatedAt?: string;
        authorities?: { authority: string }[];
        username?: string;
    };
    editable?: boolean;
    onSave?: (updatedUser: Partial<UserDetailsProps["user"]>) => void;
}

const UserDetailsDialog: React.FC<UserDetailsProps> = ({ user, editable = false, onSave }) => {
    const [formData, setFormData] = useState(user);

    const handleChange = (field: keyof UserDetailsProps["user"], value: any) => {
        setFormData({ ...formData, [field]: value });
    };

    const handleSave = () => {
        if (onSave) {
            onSave(formData);
        }
    };

    return (
        <div className="p-6 bg-background shadow rounded-md">
            {/* Header */}
            <div className="flex items-center gap-4 mb-6">
                <UserCircle className="w-16 h-16 text-blue-500" />
                <div>
                    {editable ? (
                        <input
                            type="text"
                            value={formData.username || ""}
                            onChange={(e) => handleChange("username", e.target.value)}
                            className="text-xl font-bold text-text-dark border border-border rounded px-2 py-1"
                            placeholder="Username"
                        />
                    ) : (
                        <h1 className="text-xl font-bold text-text-dark">{user.username}</h1>
                    )}
                    {editable ? (
                        <input
                            type="email"
                            value={formData.email}
                            onChange={(e) => handleChange("email", e.target.value)}
                            className="text-sm text-text border border-border rounded px-2 py-1 mt-1"
                            placeholder="Email"
                        />
                    ) : (
                        <p className="text-sm text-text">{user.email}</p>
                    )}
                </div>
            </div>

            {/* User Details */}
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <h2 className="font-semibold text-text">Account Info</h2>
                    <ul className="text-sm text-text space-y-1">
                        <li>
                            Enabled:{" "}
                            {editable ? (
                                <input
                                    type="checkbox"
                                    checked={formData.enabled}
                                    onChange={(e) => handleChange("enabled", e.target.checked)}
                                />
                            ) : (
                                user.enabled ? "Yes" : "No"
                            )}
                        </li>
                        <li>
                            Role:{" "}
                            {editable ? (
                                <select
                                    value={formData.role}
                                    onChange={(e) => handleChange("role", e.target.value)}
                                    className="border border-border rounded px-2 py-1"
                                >
                                    <option value="USER">USER</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            ) : (
                                user.role
                            )}
                        </li>
                    </ul>
                </div>
                <div>
                    <h2 className="font-semibold text-text">Login Info</h2>
                    {editable ? (
                        <div className="space-y-2">
                            <input
                                type="password"
                                placeholder="Password"
                                onChange={(e) => handleChange("password", e.target.value)}
                                className="border border-border rounded px-2 py-1 w-full"
                            />
                        </div>
                    ) : (
                        <ul className="text-sm text-text space-y-1">
                            <li>Last Login Time: {user.lastLoginTime || "N/A"}</li>
                            <li>Last Password Change: {user.lastPasswordChange || "N/A"}</li>
                        </ul>
                    )}
                </div>
            </div>

            {/* Save Button */}
            {editable && (
                <div className="mt-6">
                    <button
                        onClick={handleSave}
                        className="bg-blue-500 text-white px-4 py-2 rounded"
                    >
                        Save
                    </button>
                </div>
            )}
        </div>
    );
};

export default UserDetailsDialog;
