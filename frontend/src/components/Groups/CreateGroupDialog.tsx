import React, { useState } from "react";
import { useCreateGroup } from "../../api/generated/group-controller/group-controller";

const CreateGroupDialog = ({ open, onClose, onGroupCreated }) => {
    const [group, setGroup] = useState({ name: "", description: "" });
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const { mutate: createGroup } = useCreateGroup({
        onSuccess: (data) => {
            setSuccess("Group created successfully!");
            onGroupCreated(data);
            setTimeout(() => {
                setGroup({ name: "", description: "" });
                setSuccess("");
                onClose();
            }, 2000);
        },
        onError: (err) => {
            setError(err.response?.data?.message || "Failed to create group");
        }
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        createGroup({ data: group });
    };

    // Rest of your component remains the same
    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
            <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                <h3 className="text-lg font-medium leading-6 text-gray-900 mb-4">
                    Create New Group
                </h3>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                {success && <p className="text-green-500 mb-4">{success}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label
                            htmlFor="name"
                            className="block text-sm font-medium text-gray-700"
                        >
                            Group Name
                        </label>
                        <input
                            type="text"
                            name="name"
                            id="name"
                            value={group.name}
                            onChange={handleChange}
                            required
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        />
                    </div>
                    <div className="mb-4">
                        <label
                            htmlFor="description"
                            className="block text-sm font-medium text-gray-700"
                        >
                            Description
                        </label>
                        <textarea
                            name="description"
                            id="description"
                            value={group.description}
                            onChange={handleChange}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
                        ></textarea>
                    </div>
                    <div className="mt-5 sm:mt-6 flex justify-end">
                        <button
                            type="button"
                            onClick={handleCancel}
                            className="mr-2 inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                        >
                            Create
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroupDialog;
