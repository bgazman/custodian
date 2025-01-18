import React from "react";
import { Pencil, X, Check } from "lucide-react";

interface SectionHeaderProps {
    title: string;
    isEditing: boolean;
    onEdit: () => void;
    onSave: () => void;
    onCancel: () => void;
    loading?: boolean;
}

const SectionHeader: React.FC<SectionHeaderProps> = ({ title, isEditing, onEdit, onSave, onCancel, loading }) => (
    <div className="px-6 py-4 border-b border-border flex justify-between items-center">
        <h2 className="text-xl font-semibold">{title}</h2>
        <div>
            {isEditing ? (
                <div className="space-x-2">
                    <button
                        onClick={onCancel}
                        disabled={loading}
                        className={`inline-flex items-center px-3 py-1.5 border border-border rounded-md text-sm font-medium ${
                            loading ? "text-text-light bg-gray-100" : "text-text bg-background hover:bg-background"
                        }`}
                    >
                        <X className="h-4 w-4 mr-1" />
                        Cancel
                    </button>
                    <button
                        onClick={onSave}
                        disabled={loading}
                        className={`inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium ${
                            loading ? "bg-gray-400" : "text-white bg-blue-600 hover:bg-blue-700"
                        }`}
                    >
                        <Check className="h-4 w-4 mr-1" />
                        Save
                    </button>
                </div>
            ) : (
                <button
                    onClick={onEdit}
                    className="inline-flex items-center px-3 py-1.5 border border-border rounded-md text-sm font-medium text-text bg-background hover:bg-background"
                >
                    <Pencil className="h-4 w-4 mr-1" />
                    Edit
                </button>
            )}
        </div>
    </div>
);

export default SectionHeader;
