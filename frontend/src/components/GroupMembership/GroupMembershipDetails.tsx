import React from "react";

const GroupMembershipDetails = ({ membership }) => {
    return (
        <div>
            <h2 className="text-xl font-bold mb-4">Membership Details</h2>
            <p><strong>User ID:</strong> {membership.userId}</p>
            <p><strong>Group ID:</strong> {membership.groupId}</p>
            <p><strong>Role:</strong> {membership.role}</p>
        </div>
    );
};

export default GroupMembershipDetails;