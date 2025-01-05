import React, { useState } from 'react';
import { Mail, UserCircle, X, Phone, Lock } from 'lucide-react';
import { useUsers } from '../../hooks/userUsers';
import { useRoles } from '../../hooks/useRoles';

interface CreateUserDialogProps {
    open: boolean;
    onClose: () => void;
    onUserCreated: (user: any) => void;
}

const CreateUserDialog: React.FC<CreateUserDialogProps> = ({ open, onClose, onUserCreated }) => {
    const { createUser } = useUsers();
    const { roles, loading: rolesLoading, error: rolesError } = useRoles();

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        phoneNumber: '',
        enabled: true,
        mfaEnabled: false,
        mfaMethod: 'SMS',
        emailVerified: false,
        userRoles: []
    });

    const [error, setError] = useState('');

    const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const roleId = Number(e.target.value);
        const selectedRole = roles.find(role => role.id === roleId);
        if (selectedRole) {
            setFormData(prev => ({
                ...prev,
                userRoles: [{
                    id: { roleId },
                    role: selectedRole
                }]
            }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const newUser = await createUser(formData);
            onUserCreated(newUser);
            handleReset();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to create user');
        }
    };

    const handleReset = () => {
        setFormData({
            name: '',
            email: '',
            password: '',
            phoneNumber: '',
            enabled: true,
            mfaEnabled: false,
            mfaMethod: 'SMS',
            emailVerified: false,
            userRoles: []
        });
        setError('');
        onClose();
    };

    // Return null when dialog shouldn't be open
    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg w-full max-w-2xl">
                <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-gray-900">Create New User</h2>
                    <button
                        onClick={handleReset}
                        className="text-gray-400 hover:text-gray-500"
                        type="button"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6">
                    {error && (
                        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded">
                            {error}
                        </div>
                    )}

                    <div className="space-y-6">
                        {/* Name Field */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Full Name
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <UserCircle className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    type="text"
                                    name="name"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={formData.name}
                                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                                    placeholder="John Doe"
                                />
                            </div>
                        </div>

                        {/* Role Selection */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Role
                            </label>
                            <div className="relative">
                                {rolesLoading ? (
                                    <div className="flex items-center space-x-2 px-3 py-2 border border-gray-300 rounded-md text-gray-500">
                                        <div className="animate-spin h-4 w-4 border-2 border-indigo-500 rounded-full border-t-transparent"></div>
                                        <span>Loading roles...</span>
                                    </div>
                                ) : rolesError ? (
                                    <div className="px-3 py-2 border border-red-300 rounded-md text-red-500 bg-red-50">
                                        Failed to load roles
                                    </div>
                                ) : (
                                    <select
                                        name="role"
                                        className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                        onChange={handleRoleChange}
                                        value={formData.userRoles[0]?.role?.id || ''}
                                        required
                                    >
                                        <option value="">Select a role</option>
                                        {roles.map(role => (
                                            <option key={role.id} value={role.id}>
                                                {role.name} - {role.description}
                                            </option>
                                        ))}
                                    </select>
                                )}
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Email
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    type="email"
                                    name="email"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={formData.email}
                                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                                    placeholder="john@example.com"
                                />
                            </div>
                        </div>

                        {/* Password Field */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    type="password"
                                    name="password"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={formData.password}
                                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                                />
                            </div>
                        </div>

                        {/* Phone Number Field */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Phone Number
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Phone className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    type="tel"
                                    name="phoneNumber"
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={formData.phoneNumber}
                                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                                    placeholder="+1234567890"
                                />
                            </div>
                        </div>

                        {/* Toggles */}
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-700">Enable Account</span>
                                <input
                                    type="checkbox"
                                    checked={formData.enabled}
                                    onChange={(e) => setFormData({...formData, enabled: e.target.checked})}
                                    className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                />
                            </div>
                            <div className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-700">Enable MFA</span>
                                <input
                                    type="checkbox"
                                    checked={formData.mfaEnabled}
                                    onChange={(e) => setFormData({...formData, mfaEnabled: e.target.checked})}
                                    className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            onClick={handleReset}
                            type="button"
                            className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-indigo-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-indigo-700"
                        >
                            Create User
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateUserDialog;