import React from "react";
import { Secret } from "../../api/generated/model";

interface SecretDetailsDialogProps {
    secret: Secret;
}

const SecretDetailsDialog: React.FC<SecretDetailsDialogProps> = ({ secret }) => {
    return (
        <div>
            <h2 className="text-xl font-bold mb-4">Secret Details</h2>
            <p><strong>ID:</strong> {secret.id}</p>
            <p><strong>Name:</strong> {secret.name}</p>
            <p><strong>Type:</strong> {secret.type}</p>
            <p><strong>Status:</strong> {secret.active ? 'Active' : 'Inactive'}</p>
            <p><strong>Public Key:</strong> {secret.publicKey || "No public key available"}</p>
            <p><strong>Private Key:</strong> {secret.privateKey ? '****' : "No private key available"}</p>
            <p><strong>Last Rotated:</strong> {secret.lastRotatedAt ? new Date(secret.lastRotatedAt).toLocaleString() : '-'}</p>
            <p><strong>Expires At:</strong> {secret.expiresAt ? new Date(secret.expiresAt).toLocaleString() : '-'}</p>
            <p><strong>Created At:</strong> {secret.createdAt ? new Date(secret.createdAt).toLocaleString() : '-'}</p>
            <p><strong>Updated At:</strong> {secret.updatedAt ? new Date(secret.updatedAt).toLocaleString() : '-'}</p>
        </div>
    );
};

export default SecretDetailsDialog;