import React from "react";

interface EditableInfoItemProps {
    label: string;
    value: string | number | boolean | string[]; // Updated to allow string arrays for multi-select
    isEditing: boolean;
    onEdit: (value: string | number | boolean | string[]) => void;
    editable?: boolean;
    type?: "text" | "number" | "radio" | "checkbox" | "dropdown";
    radioOptions?: { id: string; label: string }[];
    dropdownOptions?: { id: string; label: string }[];
    disabled?: boolean;
    multiple?: boolean; // Added support for multi-select dropdowns
}

const EditableInfoItem: React.FC<EditableInfoItemProps> = ({
                                                               label,
                                                               value,
                                                               isEditing,
                                                               onEdit,
                                                               editable = true,
                                                               type = "text",
                                                               radioOptions = [],
                                                               dropdownOptions = [],
                                                               disabled = false,
                                                               multiple = false, // Default to single-select dropdown
                                                           }) => {
    if (type === "radio") {
        return (
            <div>
                <dt className="text-sm font-medium text-text-muted">{label}</dt>
                <div className="mt-1 flex gap-4">
                    {radioOptions.map((option) => (
                        <label key={option.id} className="inline-flex items-center">
                            <input
                                type="radio"
                                value={option.id}
                                checked={value === option.id}
                                onChange={() => isEditing && onEdit(option.id)}
                                disabled={!isEditing || disabled}
                                className="form-radio"
                            />
                            <span className={`ml-2 ${!isEditing ? "text-text-light" : ""}`}>
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
                <dt className="text-sm font-medium text-text-muted">{label}</dt>
                <input
                    type="checkbox"
                    checked={value as boolean}
                    onChange={(e) => onEdit(e.target.checked)}
                    disabled={!isEditing || disabled}
                    className="mt-1 block w-6 h-6"
                />
            </div>
        );
    }

    if (type === "dropdown") {
        return (
            <div>
                <dt className="text-sm font-medium text-text-muted">{label}</dt>
                <select
                    multiple={multiple} // Enable multi-select if the `multiple` prop is true
                    value={
                        multiple
                            ? (value as string[]) ?? [] // Ensure it's an array for multi-select
                            : (value as string) ?? "" // Single-select default to string
                    }
                    onChange={(e) => {
                        if (multiple) {
                            const selectedOptions = Array.from(
                                e.target.selectedOptions,
                                (option) => option.value
                            );
                            onEdit(selectedOptions); // Pass selected values as an array
                        } else {
                            onEdit(e.target.value === "" ? null : e.target.value); // Single-select handling
                        }
                    }}
                    disabled={!isEditing || disabled}
                    className="mt-1 block w-full rounded-md border border-border py-1.5 px-3 text-text-dark shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                >
                    {!multiple && <option value="">None</option>} {/* Single-select default */}
                    {dropdownOptions.map((option) => (
                        <option key={option.id} value={option.id}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>
        );
    }


    if (!editable || !isEditing) {
        return (
            <div>
                <dt className="text-sm font-medium text-text-muted">{label}</dt>
                <dd className="mt-1 text-sm text-text-dark">{value || "Not Set"}</dd>
            </div>
        );
    }

    return (
        <div>
            <dt className="text-sm font-medium text-text-muted">{label}</dt>
            <input
                type={type === "number" ? "number" : "text"}
                className="mt-1 block w-full rounded-md border border-border py-1.5 px-3 text-text-dark shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                value={value as string | number}
                onChange={(e) => {
                    const val = type === "number" ? +e.target.value : e.target.value;
                    if (type === "number" && isNaN(val as number)) return;
                    onEdit(val);
                }}
                disabled={disabled}
            />
        </div>
    );
};

export default EditableInfoItem;
