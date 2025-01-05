import React from 'react';
import { X } from 'lucide-react';

interface DeleteUserDialogProps {
    open: boolean;
    onClose: () => void;
    onConfirm: () => void;
    userName: string;
}

const DeleteUserDialog: React.FC<DeleteUserDialogProps> = ({ open, onClose, onConfirm, userName }) => {
    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg w-full max-w-md">
                <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                    <h2 className="text-xl font-semibold text-gray-900">Delete User</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-500"
                        type="button"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <div className="p-6">
                    <div className="mb-4 text-gray-700">
                        Are you sure you want to delete user <span className="font-semibold">{userName}</span>?
                        This action cannot be undone.
                    </div>

                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            onClick={onClose}
                            type="button"
                            className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            onClick={onConfirm}
                            type="button"
                            className="px-4 py-2 bg-red-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-red-700"
                        >
                            Delete User
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DeleteUserDialog;