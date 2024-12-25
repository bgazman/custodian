import React, { useState } from "react";
import { ApiClient } from "../../api/common/ApiClient";

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
            const response = await ApiClient.post("/group_memberships", membership);
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
            <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                <h3 className="text-lg font-medium leading-6 text-gray-900 mb-4">Add Group Membership</h3>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                {success && <p className="text-green-500 mb-4">{success}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="userId" className="block text-sm font-medium text-gray-700">
                            User ID
                        </label>
                        <input
                            type="number"
                            name="userId"
                            id="userId"
                            value={membership.userId}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="groupId" className="block text-sm font-medium text-gray-700">
                            Group ID
                        </label>
                        <input
                            type="number"
                            name="groupId"
                            id="groupId"
                            value={membership.groupId}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="role" className="block text-sm font-medium text-gray-700">
                            Role
                        </label>
                        <select
                            name="role"
                            id="role"
                            value={membership.role}
                            onChange={handleChange}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        >
                            <option value="ADMIN">Admin</option>
                            <option value="MEMBER">Member</option>
                        </select>
                    </div>
                    <div className="mt-5 sm:mt-6 flex justify-end">
                        <button
                            type="button"
                            onClick={onClose}
                            className="mr-2 inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
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