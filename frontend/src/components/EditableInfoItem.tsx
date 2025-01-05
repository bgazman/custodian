import React, { useState } from "react";
import { Plus, Trash } from "lucide-react";



interface EditableInfoItemProps {
    label: string;
    value: string | number | boolean ;
    isEditing: boolean;
    onEdit: (value: string | number | boolean ) => void;
    editable?: boolean;
    type?: "text" | "number" | "radio" | "checkbox";
    radioOptions?: { id: string; label: string }[];
}

const EditableInfoItem: React.FC<EditableInfoItemProps> = ({
                                                               label,
                                                               value,
                                                               isEditing,
                                                               onEdit,
                                                               editable = true,
                                                               type = "text",
                                                               radioOptions = [],
                                                           }) => {



    if (type === "radio") {
        return (
            <div>
                <dt className="text-sm font-medium text-gray-500">{label}</dt>
                <div className="mt-1 flex gap-4">
                    {radioOptions.map((option) => (
                        <label key={option.id} className="inline-flex items-center">
                            <input
                                type="radio"
                                value={option.id}
                                checked={value === option.id}
                                onChange={() => isEditing && onEdit(option.id)}
                                disabled={!isEditing}
                                className="form-radio"
                            />
                            <span className={`ml-2 ${!isEditing ? "text-gray-400" : ""}`}>
                                {option.label}
                            </span>
                        </label>
                    ))}
                </div>
            </div>
        );
    }

    if (type === "checkbox") {
        return (
            <div>
                <dt className="text-sm font-medium text-gray-500">{label}</dt>
                <input
                    type="checkbox"
                    checked={value as boolean}
                    onChange={(e) => onEdit(e.target.checked)}
                    disabled={!isEditing}
                    className="mt-1 block w-6 h-6"
                />
            </div>
        );
    }

    if (!editable || !isEditing) {
        return (
            <div>
                <dt className="text-sm font-medium text-gray-500">{label}</dt>
                <dd className="mt-1 text-sm text-gray-900">{value}</dd>
            </div>
        );
    }

    return (
        <div>
            <dt className="text-sm font-medium text-gray-500">{label}</dt>
            <input
                type={type === "number" ? "number" : "text"}
                className="mt-1 block w-full rounded-md border border-gray-300 py-1.5 px-3 text-gray-900 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                value={value as string | number}
                onChange={(e) => {
                    const val = type === "number" ? +e.target.value : e.target.value;
                    if (type === "number" && isNaN(val as number)) return;
                    onEdit(val);
                }}
            />
        </div>
    );
};

export default EditableInfoItem;