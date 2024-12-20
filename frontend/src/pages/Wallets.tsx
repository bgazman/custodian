import React, { useMemo } from 'react';
import { Plus, TrendingUp } from 'lucide-react';
import WalletCard from '../components/Wallet/WalletCard';
import { Wallet } from '../types/Wallet'; // Import the Wallet type from your types file

const Wallets: React.FC = () => {
    // Wallet data
    const wallets: Wallet[] = [
        {
            id: '1',
            address: '0x123456789abcdef',
            type: 'hot',
            currency: 'USD',
            balance: 4562.5,
            status: 'active',
            createdAt: new Date(),
            updatedAt: new Date(),
        },
        {
            id: '2',
            address: '0xabcdef123456789',
            type: 'cold',
            currency: 'USD',
            balance: 8091.78,
            status: 'active',
            createdAt: new Date(),
            updatedAt: new Date(),
        },
    ];

    // Derived values
    const totalValue = useMemo(
        () => wallets.reduce((sum, wallet) => sum + wallet.balance, 0),
        [wallets]
    );

    const avgPercentageChange = useMemo(() => {
        const percentageChanges = wallets.map((wallet) => wallet.balance * 0.02); // Mock percentage logic
        return percentageChanges.reduce((sum, change) => sum + change, 0) / wallets.length;
    }, [wallets]);

    return (
        <div className="p-6 max-w-6xl mx-auto">
            {/* Portfolio Summary */}
            <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
                <h3 className="text-gray-600 text-sm">Total Portfolio Value</h3>
                <div className="flex items-baseline gap-3 mt-2">
          <span className="text-3xl font-bold">
            {totalValue.toLocaleString('en-US', { style: 'currency', currency: 'USD' })}
          </span>
                    <span className="text-green-600 text-sm flex items-center">
            <TrendingUp className="h-4 w-4 mr-1" />
                        {avgPercentageChange > 0 ? '+' : ''}{avgPercentageChange.toFixed(1)}%
          </span>
                </div>
            </div>

            {/* Wallet Header */}
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold">My Wallets</h2>
                <button
                    className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"
                    aria-label="Add Wallet"
                >
                    <Plus className="h-4 w-4" />
                    Add Wallet
                </button>
            </div>

            {/* Wallet List */}
            <div className="grid md:grid-cols-2 gap-6">
                {wallets.map((wallet) => (
                    <WalletCard
                        key={wallet.id}
                        wallet={wallet}
                        onSend={() => console.log('Send from wallet', wallet.id)}
                        onReceive={() => console.log('Receive to wallet', wallet.id)}
                    />
                ))}
            </div>
        </div>
    );
};

export default Wallets;
