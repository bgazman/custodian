import React from "react";
import { Group } from "../../types/Group";

interface GroupDetailsProps {
    group: Group;
}

const GroupDetails: React.FC<GroupDetailsProps> = ({ group }) => {
    return (
        <div>
            <h2 className="text-xl font-bold mb-4">Group Details</h2>
            <p><strong>ID:</strong> {group.id}</p>
            <p><strong>Name:</strong> {group.name}</p>
            <p><strong>Description:</strong> {group.description || "No description available"}</p>
            <p><strong>Created At:</strong> {new Date(group.createdAt).toLocaleString()}</p>
            <p><strong>Updated At:</strong> {new Date(group.updatedAt).toLocaleString()}</p>
        </div>
    );
};

export default GroupDetails;
