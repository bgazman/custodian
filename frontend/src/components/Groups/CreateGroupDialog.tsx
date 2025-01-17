import React, { useState } from "react";
import { useCreateGroup } from "../../api/generated/group-controller/group-controller";
import { Group } from "../../api/generated/model";
import { X } from 'lucide-react';

interface CreateGroupDialogProps {
    open: boolean;
    onClose: () => void;
}

const CreateGroupDialog: React.FC<CreateGroupDialogProps> = ({ open, onClose }) => {
    const [formData, setFormData] = useState<Partial<Group>>({
        name: '',
        description: '',
    });
    const [error, setError] = useState('');

    const { mutate: createGroup, isPending } = useCreateGroup();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        createGroup({
            data: formData
        }, {
            onSuccess: () => {
                onClose();
                setFormData({ name: '', description: '' });
            },
            onError: (err: Error) => {
                setError(err.message || 'Failed to create group');
            }
        });
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg w-full max-w-2xl">
                <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-gray-900">Create New Group</h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-500">
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
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Group Name
                            </label>
                            <input
                                type="text"
                                required
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                value={formData.name}
                                onChange={(e) => setFormData({...formData, name: e.target.value})}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Description
                            </label>
                            <textarea
                                className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                                value={formData.description}
                                onChange={(e) => setFormData({...formData, description: e.target.value})}
                                rows={4}
                            />
                        </div>
                    </div>

                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isPending}
                            className="px-4 py-2 bg-indigo-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-50"
                        >
                            {isPending ? 'Creating...' : 'Create Group'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroupDialog;