import React, { useState } from "react";
// import { ApiClient } from "../../api/common/ApiClient";

const CreateGroupMembershipDialog = ({ open, onClose, onMembershipCreated }) => {
    const initialMembershipState = {
        userId: "",
        groupId: "",
        role: "MEMBER", // Default role
    };

    const [membership, setMembership] = useState(initialMembershipState);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleChange = (e) => {
        const { name, value } = e.target;
        setMembership((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        try {
            const response = null;
            setSuccess("Membership created successfully!");
            onMembershipCreated(response.data);
            setTimeout(() => {
                setMembership(initialMembershipState);
                setSuccess("");
                onClose();
            }, 2000);
        } catch (error) {
            console.error("Error creating membership:", error);
            setError(error.response?.data?.message || "An error occurred while creating the membership.");
        }
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
            <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-background">
                <h3 className="text-lg font-medium leading-6 text-text-dark mb-4">Add Group Membership</h3>
                {error && <p className="text-error mb-4">{error}</p>}
                {success && <p className="text-green-500 mb-4">{success}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="userId" className="block text-sm font-medium text-text">
                            User ID
                        </label>
                        <input
                            type="number"
                            name="userId"
                            id="userId"
                            value={membership.userId}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full rounded-md border-border shadow-sm focus:border-primary/30 focus:ring-primary/20 focus:ring-opacity-50"
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="groupId" className="block text-sm font-medium text-text">
                            Group ID
                        </label>
                        <input
                            type="number"
                            name="groupId"
                            id="groupId"
                            value={membership.groupId}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full rounded-md border-border shadow-sm focus:border-primary/30 focus:ring focus:ring-primary/20 focus:ring-opacity-50"
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="role" className="block text-sm font-medium text-text">
                            Role
                        </label>
                        <select
                            name="role"
                            id="role"
                            value={membership.role}
                            onChange={handleChange}
                            className="mt-1 block w-full rounded-md border-border shadow-sm focus:border-primary/30 focus:ring ring-primary/20 focus:ring-opacity-50"
                        >
                            <option value="ADMIN">Admin</option>
                            <option value="MEMBER">Member</option>
                        </select>
                    </div>
                    <div className="mt-5 sm:mt-6 flex justify-end">
                        <button
                            type="button"
                            onClick={onClose}
                            className="mr-2 inline-flex justify-center rounded-md border border-border shadow-sm px-4 py-2 bg-background text-base font-medium text-text hover:bg-background focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary sm:text-sm"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-primary text-base font-medium text-white hover:bg-primary focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary sm:text-sm"
                        >
                            Add Membership
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroupMembershipDialog;