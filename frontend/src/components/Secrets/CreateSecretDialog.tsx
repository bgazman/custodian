import React, { useState } from "react";
import { Secret } from "../../api/generated/model";
import { useCreateSecret } from "../../api/generated/secret-controller/secret-controller";
import { X } from 'lucide-react';

interface CreateSecretDialogProps {
    open: boolean;
    onClose: () => void;
}

const CreateSecretDialog: React.FC<CreateSecretDialogProps> = ({ open, onClose }) => {
    const [formData, setFormData] = useState<Partial<Secret>>({
        name: '',
        type: '',
        publicKey: '',
        privateKey: '',
        active: true,
        expiresAt: ''
    });
    const [error, setError] = useState('');

    const { mutate: createSecret, isPending } = useCreateSecret();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        createSecret({
            data: formData
        }, {
            onSuccess: () => {
                onClose();
                setFormData({ name: '', type: '', publicKey: '', privateKey: '', active: true, expiresAt: '' });
            },
            onError: (err: Error) => {
                setError(err.message || 'Failed to create secret');
            }
        });
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-background rounded-lg w-full max-w-2xl">
                <div className="px-6 py-4 border-b border-border flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-text-dark">Create New Secret</h2>
                    <button onClick={onClose} className="text-text-light hover:text-text-muted">
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
                                Secret Name
                            </label>
                            <input
                                type="text"
                                required
                                className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                value={formData.name}
                                onChange={(e) => setFormData({...formData, name: e.target.value})}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Type
                            </label>
                            <select
                                required
                                className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                value={formData.type}
                                onChange={(e) => setFormData({...formData, type: e.target.value})}
                            >
                                <option value="">Select type</option>
                                <option value="SSH">SSH</option>
                                <option value="API">API</option>
                                <option value="TOKEN">TOKEN</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Public Key
                            </label>
                            <textarea
                                className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                value={formData.publicKey}
                                onChange={(e) => setFormData({...formData, publicKey: e.target.value})}
                                rows={3}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Private Key
                            </label>
                            <textarea
                                className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                value={formData.privateKey}
                                onChange={(e) => setFormData({...formData, privateKey: e.target.value})}
                                rows={3}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-text mb-1">
                                Expires At
                            </label>
                            <input
                                type="datetime-local"
                                className="block w-full px-3 py-2 border border-border rounded-md focus:ring-2 focus:ring-primary focus:border-primary"
                                value={formData.expiresAt}
                                onChange={(e) => setFormData({...formData, expiresAt: e.target.value})}
                            />
                        </div>

                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="active"
                                className="h-4 w-4 text-primary focus:ring-primary border-border rounded"
                                checked={formData.active}
                                onChange={(e) => setFormData({...formData, active: e.target.checked})}
                            />
                            <label htmlFor="active" className="ml-2 block text-sm text-text">
                                Active
                            </label>
                        </div>
                    </div>

                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-border rounded-md text-sm font-medium text-text hover:bg-background"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isPending}
                            className="px-4 py-2 bg-primary border border-transparent rounded-md text-sm font-medium text-white hover:bg-primary disabled:opacity-50"
                        >
                            {isPending ? 'Creating...' : 'Create Secret'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateSecretDialog;