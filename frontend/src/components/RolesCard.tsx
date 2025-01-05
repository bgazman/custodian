import React from 'react';

const RolesCard = ({ roles = [] }) => {
    if (!roles.length) {
        return (
            <div className="bg-white shadow rounded-lg">
                <div className="px-6 py-4 border-b border-gray-200">
                    <h2 className="text-xl font-semibold">Roles</h2>
                </div>
                <div className="p-6">
                    <p className="text-gray-500">No roles assigned</p>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-xl font-semibold">Roles</h2>
            </div>
            <div className="p-6">
                <div className="flex flex-wrap gap-2">
                    {roles.map(userRole => (
                        <span
                            key={userRole.role.id}
                            className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                        >
                   {userRole.role.name}
               </span>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default RolesCard;