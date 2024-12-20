import React, { useState } from 'react';
import { Lock, Send, QrCode, AlertTriangle, Eye, EyeOff } from 'lucide-react';
import { Wallet } from '../../types/Wallet'; // Assuming Wallet is imported from your types file

interface WalletCardProps {
    wallet: Wallet; // Use the Wallet type directly
    onSend?: () => void;
    onReceive?: () => void;
}

const WalletCard: React.FC<WalletCardProps> = ({ wallet, onSend, onReceive }) => {
    const [showBalance, setShowBalance] = useState(false);

    const toggleBalance = () => setShowBalance(!showBalance);

    const maskedAmount = '••••••';

    const isProtected = wallet.status === 'active'; // Example logic for security status

    return (
        <div className="bg-white rounded-xl shadow-sm border p-5 max-w-md">
            <div className="flex justify-between items-center mb-4">
                <h3 className="font-semibold text-lg">Wallet: {wallet.type}</h3>
                <div
                    className={`flex items-center gap-1 ${
                        isProtected ? 'text-green-600 bg-green-50' : 'text-amber-600 bg-amber-50'
                    } px-3 py-1 rounded-full text-sm`}
                >
                    <Lock className="h-4 w-4" />
                    {isProtected ? 'Protected' : 'Not Protected'}
                </div>
            </div>

            <div className="bg-gray-50 rounded-lg p-4 mb-4">
                <div className="flex justify-between items-center mb-1">
                    <p className="text-gray-600 text-sm">Available Balance</p>
                    <button
                        onClick={toggleBalance}
                        className="text-gray-500 hover:text-gray-700 p-1"
                        aria-label={showBalance ? 'Hide Balance' : 'Show Balance'}
                    >
                        {showBalance ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    </button>
                </div>
                <div className="flex items-baseline gap-2">
          <span className="text-2xl font-bold">
            {showBalance
                ? wallet.balance.toLocaleString('en-US', {
                    style: 'currency',
                    currency: wallet.currency,
                })
                : `${wallet.currency} ${maskedAmount}`}
          </span>
                </div>
                <p className="text-gray-500 text-sm mt-1">
                    Last activity: {/* Replace with actual last activity if stored */}
                    Not Available
                </p>
            </div>

            <div className="grid grid-cols-2 gap-3 mb-4">
                <button
                    onClick={onSend}
                    className="flex items-center justify-center gap-2 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700"
                >
                    <Send className="h-4 w-4" />
                    Send
                </button>
                <button
                    onClick={onReceive}
                    className="flex items-center justify-center gap-2 bg-gray-100 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-200"
                >
                    <QrCode className="h-4 w-4" />
                    Receive
                </button>
            </div>

            {wallet.status === 'locked' && (
                <div className="flex items-center gap-2 text-amber-600 bg-amber-50 p-3 rounded-lg text-sm">
                    <AlertTriangle className="h-4 w-4" />
                    <p>Your wallet is locked. Please contact support.</p>
                </div>
            )}
        </div>
    );
};

export default WalletCard;
