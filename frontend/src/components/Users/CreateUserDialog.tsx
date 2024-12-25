import React, { useState } from 'react';
import {ApiClient} from "../../api/common/ApiClient.ts";
import {Mail, UserCircle, X} from "lucide-react";
import {useUsers} from "../../hooks/userUsers";
import {User} from "../../types/User.ts";
import {useGroups} from "../../hooks/userGroups";

type CreateUserDialogProps = {
    open: boolean;
    onClose: () => void;
    onUserCreated: (user: User) => void;
}

const CreateUserDialog = ({ open, onClose, onUserCreated }: CreateUserDialogProps) => {
    const { createUser } = useUsers();
    const { groups } = useGroups();

    const [user, setUser] = useState({
        email: '',
        password: '',
        enabled: true,
        role: '',
        groups: [] as string[]
    });

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value, type } = e.target;
        const checked = (e.target as HTMLInputElement).checked;

        setUser(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            const newUser = await createUser(user);
            onUserCreated(newUser);
            handleCancel();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to create user');
        }
    };

    const handleCancel = () => {
        setUser({ email: '', password: '', enabled: true, role: 'USER', groups: [] });
        setError('');
        setSuccess('');
        onClose();
    };
    const [selectedGroups, setSelectedGroups] = useState<string[]>([]);

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white rounded-lg w-full max-w-2xl">
                {/* Header */}
                <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-gray-900">Create New User</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-500">
                        <X className="w-5 h-5"/>
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="p-6">
                    <div className="space-y-6">
                        {/* Email */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Email
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-gray-400"/>
                                </div>
                                <input
                                    type="email"
                                    required
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={user.email}
                                    onChange={(e) => setUser({...user, email: e.target.value})}
                                />
                            </div>
                        </div>

                        {/* Role */}
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Role
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <UserCircle className="h-5 w-5 text-gray-400"/>
                                </div>
                                <select
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                    value={user.role}
                                    onChange={(e) => setUser({...user, role: e.target.value})}
                                >
                                    {roles.map(role => (
                                        <option key={role.id} value={role.name}>
                                            {role.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Toggles */}
                        <div className="space-y-4">
                            {/* Status */}
                            <div className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-700">Status</span>
                                <button
                                    type="button"
                                    onClick={() => setUser({...user, isEnabled: !user.enabled})}
                                    className={`relative inline-flex h-6 w-11 items-center rounded-full
                    ${user.enabled ? 'bg-indigo-600' : 'bg-gray-200'}`}
                                >
                  <span className={`inline-block h-4 w-4 transform rounded-full bg-white transition
                    ${user.enabled ? 'translate-x-6' : 'translate-x-1'}`}
                  />
                                </button>
                            </div>

                            {/* MFA */}
                            <div className="flex items-center justify-between">
                                <span className="text-sm font-medium text-gray-700">Require MFA</span>
                                <button
                                    type="button"
                                    onClick={() => setUser({...user, requireMFA: !user.requireMFA})}
                                    className={`relative inline-flex h-6 w-11 items-center rounded-full
                    ${user.requireMFA ? 'bg-indigo-600' : 'bg-gray-200'}`}
                                >
                  <span className={`inline-block h-4 w-4 transform rounded-full bg-white transition
                    ${user.requireMFA ? 'translate-x-6' : 'translate-x-1'}`}
                  />
                                </button>
                            </div>
                        </div>
                    </div>
                </form>

                {/* Footer */}
                <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-end space-x-3 rounded-b-lg">
                    <button
                        onClick={handleCancel}
                        className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSubmit}
                        className="px-4 py-2 bg-indigo-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-indigo-700"
                    >
                        Create User
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateUserDialog;
