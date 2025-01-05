import React from 'react';
import { User } from '../../types/User';

const InfoItem = ({ label, value }: { label: string; value: string }) => (
    <div>
        <dt className="text-sm font-medium text-gray-500">{label}</dt>
        <dd className="mt-1 text-sm text-gray-900">{value}</dd>
    </div>
);

const UserSecurityDetails = ({ user }: { user: User }) => (
    <div className="space-y-6">
        <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-xl font-semibold">MFA Settings</h2>
            </div>
            <div className="p-6 grid grid-cols-2 gap-6">
                <InfoItem label="MFA Status" value={user.mfaEnabled ? 'Enabled' : 'Disabled'} />
                <InfoItem label="MFA Method" value={user.mfaMethod || 'Not Set'} />
                <InfoItem label="Backup Codes" value={user.mfaBackupCodes?.length ? 'Generated' : 'Not Generated'} />
            </div>
        </div>

        <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-xl font-semibold">Password & Credentials</h2>
            </div>
            <div className="p-6 grid grid-cols-2 gap-6">
                <InfoItem label="Last Password Change" value={user.lastPasswordChange ? new Date(user.lastPasswordChange).toLocaleString() : 'Never'} />
                <InfoItem label="Account Expiry" value={user.accountNonExpired ? 'Active' : 'Expired'} />
                <InfoItem label="Credentials Status" value={user.credentialsNonExpired ? 'Valid' : 'Expired'} />
            </div>
        </div>

        <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
                <h2 className="text-xl font-semibold">Access Control</h2>
            </div>
            <div className="p-6 space-y-4">
                <div className="border-b pb-4">
                    <h3 className="text-lg font-medium mb-2">Roles</h3>
                    <div className="flex flex-wrap gap-2">
                        {user.userRoles.map(role => (
                            <span key={role.role.id} className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">
                                {role.role.name}
                            </span>
                        ))}
                    </div>
                </div>

                <div className="border-b pb-4">
                    <h3 className="text-lg font-medium mb-2">Groups</h3>
                    <div className="flex flex-wrap gap-2">
                        {user.groupMemberships?.map(membership => (
                            <span key={membership.group.id} className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm">
                                {membership.group.name}
                            </span>
                        ))}
                    </div>
                </div>

                <div>
                    <h3 className="text-lg font-medium mb-2">Effective Permissions</h3>
                    <div className="flex flex-wrap gap-2">
                        {user.effectivePermissions?.map(permission => (
                            <span key={permission.id} className="px-3 py-1 bg-gray-100 text-gray-800 rounded-full text-sm">
                                {permission.name}
                            </span>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    </div>
);

export default UserSecurityDetails;