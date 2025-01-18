import React, { useState } from 'react';
import { Mail, UserCircle, X, Phone, Lock } from 'lucide-react';
import { useCreateUser } from "../../api/generated/user-controller/user-controller";
import { getAllRoles } from '../../api/generated/role-controller/role-controller';
import { useQuery } from "@tanstack/react-query";
import {UserCreateRequest} from "../../api/generated/model";

interface CreateUserDialogProps {
    open: boolean;
    onClose: () => void;
    onUserCreated: (user: UserCreateRequest) => void;
}

const CreateUserDialog: React.FC<CreateUserDialogProps> = ({ open, onClose, onUserCreated }) => {
    const { mutate: createUser } = useCreateUser();
    const { data: roles = [], isLoading: rolesLoading, isError: rolesError } = useQuery({
        queryKey: ['roles'],
        queryFn: async () => {
            try {
                const response = await getAllRoles();
                return Array.isArray(response) ? response : response.data || [];
            } catch (error) {
                console.error('Error fetching roles:', error);
                throw error;
            }
        }
    });

    const [formData, setFormData] = useState<UserCreateRequest>({
        name: '',
        email: '',
        password: '',
        phoneNumber: '',
        roleIds: []
    });

    const [error, setError] = useState('');

    const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const roleId = Number(e.target.value);
        if (roleId) {
            setFormData(prev => ({
                ...prev,
                roleIds: [roleId]
            }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createUser({ data: formData });
            onUserCreated(formData);
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
            roleIds: []
        });
        setError('');
        onClose();
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-background rounded-lg w-full max-w-2xl">
                <div className="px-6 py-4 border-b border-border flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-text-dark">Create New User</h2>
                    <button
                        onClick={handleReset}
                        className="text-text-light hover:text-text-muted"
                        type="button"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6">
                    {error && (
                        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-error rounded">
                            {error}
                        </div>
                    )}

                    <div className="space-y-6">
                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Full Name
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <UserCircle className="h-5 w-5 text-text-light" />
                                </div>
                                <input
                                    type="text"
                                    name="name"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                    value={formData.name}
                                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                                    placeholder="John Doe"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Role
                            </label>
                            <div className="relative">
                                {rolesLoading ? (
                                    <div className="flex items-center space-x-2 px-3 py-2 border border-border rounded-md text-text-muted">
                                        <div className="animate-spin h-4 w-4 border-2 border-primary rounded-full border-t-transparent"></div>
                                        <span>Loading roles...</span>
                                    </div>
                                ) : rolesError ? (
                                    <div className="px-3 py-2 border border-red-300 rounded-md text-error bg-red-50">
                                        Failed to load roles
                                    </div>
                                ) : (
                                    <select
                                        name="role"
                                        className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                        onChange={handleRoleChange}
                                        value={formData.roleIds?.[0] || ''}
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
                            <label className="block text-sm font-medium text-text mb-1">
                                Email
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-text-light" />
                                </div>
                                <input
                                    type="email"
                                    name="email"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                    value={formData.email}
                                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                                    placeholder="john@example.com"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-text-light" />
                                </div>
                                <input
                                    type="password"
                                    name="password"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                    value={formData.password}
                                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Phone Number
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Phone className="h-5 w-5 text-text-light" />
                                </div>
                                <input
                                    type="tel"
                                    name="phoneNumber"
                                    className="block w-full pl-10 pr-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                    value={formData.phoneNumber}
                                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                                    placeholder="+1234567890"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            onClick={handleReset}
                            type="button"
                            className="px-4 py-2 border border-border rounded-md text-sm font-medium text-text hover:bg-background"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-primary border border-transparent rounded-md text-sm font-medium text-white hover:bg-primary"
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