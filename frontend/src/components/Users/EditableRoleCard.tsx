import React, { useState } from 'react';
import { Pencil, X, Check, Plus, Trash } from 'lucide-react';

const EditableRolesCard = ({ roles, onUpdate }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editedRoles, setEditedRoles] = useState(roles);

    const handleSave = async () => {
        await onUpdate(editedRoles);
        setIsEditing(false);
    };

    return (
        <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
                <h2 className="text-xl font-semibold">Roles</h2>
                <div>
                    {isEditing ? (
                        <div className="space-x-2">
                            <button onClick={() => {
                                setEditedRoles(roles);
                                setIsEditing(false);
                            }} className="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                                <X className="h-4 w-4 mr-1" />
                                Cancel
                            </button>
                            <button onClick={handleSave} className="inline-flex items-center px-3 py-1.5 border border-transparent rounded-md text-sm font-medium text-white bg-blue-600 hover:bg-blue-700">
                                <Check className="h-4 w-4 mr-1" />
                                Save
                            </button>
                        </div>
                    ) : (
                        <button onClick={() => setIsEditing(true)} className="inline-flex items-center px-3 py-1.5 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                            <Pencil className="h-4 w-4 mr-1" />
                            Edit
                        </button>
                    )}
                </div>
            </div>
            <div className="p-6">
                <div className="flex flex-wrap gap-2">
                    {editedRoles.map(role => (
                        <div key={role.role.id} className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                            {role.role.name}
                            {isEditing && (
                                <button onClick={() => setEditedRoles(editedRoles.filter(r => r.role.id !== role.role.id))} className="ml-2">
                                    <Trash className="h-3 w-3 text-blue-600 hover:text-blue-800" />
                                </button>
                            )}
                        </div>
                    ))}
                    {isEditing && (
                        <button className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium border-2 border-dashed border-gray-300 text-gray-500 hover:border-gray-400 hover:text-gray-600">
                            <Plus className="h-4 w-4 mr-1" />
                            Add Role
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default EditableRolesCard;