import React from 'react';
import { Wallet } from '../../types/Wallet'; // Import Wallet type

export interface WalletTableProps {
    wallets: Wallet[];
}

const WalletTable: React.FC<WalletTableProps> = ({ wallets }) => {
    return (
        <div className="overflow-x-auto">
            <table className="min-w-full bg-white shadow-md rounded-lg">
                <thead className="bg-gray-50 border-b">
                <tr>
                    <th className="text-left px-6 py-3 text-sm font-semibold text-gray-600">Type</th>
                    <th className="text-left px-6 py-3 text-sm font-semibold text-gray-600">Address</th>
                    <th className="text-left px-6 py-3 text-sm font-semibold text-gray-600">Currency</th>
                    <th className="text-left px-6 py-3 text-sm font-semibold text-gray-600">Balance</th>
                    <th className="text-left px-6 py-3 text-sm font-semibold text-gray-600">Status</th>
                </tr>
                </thead>
                <tbody>
                {wallets.map((wallet) => (
                    <tr
                        key={wallet.id}
                        className="border-b hover:bg-gray-100 transition-colors"
                    >
                        <td className="px-6 py-4 text-sm text-gray-800">{wallet.type}</td>
                        <td className="px-6 py-4 text-sm text-gray-800 truncate">{wallet.address}</td>
                        <td className="px-6 py-4 text-sm text-gray-800">{wallet.currency}</td>
                        <td className="px-6 py-4 text-sm text-gray-800">
                            {wallet.balance.toLocaleString('en-US', { style: 'currency', currency: wallet.currency })}
                        </td>
                        <td
                            className={`px-6 py-4 text-sm font-medium ${
                                wallet.status === 'active'
                                    ? 'text-green-600'
                                    : wallet.status === 'locked'
                                        ? 'text-red-600'
                                        : 'text-gray-600'
                            }`}
                        >
                            {wallet.status}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default WalletTable;
