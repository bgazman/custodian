import React from "react";
import { Permission } from "../../api/generated/model";

interface PermissionDetailsDialogProps {
    permission: Permission;
}

const PermissionDetailsDialog: React.FC<PermissionDetailsDialogProps> = ({ permission }) => {
    return (
        <div>
            <h2 className="text-xl font-bold mb-4">Permission Details</h2>
            <p><strong>ID:</strong> {permission.id}</p>
            <p><strong>Name:</strong> {permission.name}</p>
            <p><strong>Description:</strong> {permission.description || "No description available"}</p>
            <p><strong>Created At:</strong> {permission.createdAt ? new Date(permission.createdAt).toLocaleString() : '-'}</p>
            <p><strong>Updated At:</strong> {permission.updatedAt ? new Date(permission.updatedAt).toLocaleString() : '-'}</p>
        </div>
    );
};

export default PermissionDetailsDialog;