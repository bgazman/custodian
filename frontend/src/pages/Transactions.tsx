import React from 'react';

const Transactions = () => {
    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-6">Settings</h2>
            <div className="bg-white rounded-lg shadow p-6">
                <div className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Email Notifications</label>
                        <div className="mt-2">
                            <label className="inline-flex items-center">
                                <input type="checkbox" className="rounded border-gray-300 text-blue-600" />
                                <span className="ml-2">Enable email notifications</span>
                            </label>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Theme</label>
                        <select className="mt-2 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
                            <option>Light</option>
                            <option>Dark</option>
                            <option>System</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Transactions;