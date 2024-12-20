import React, { useEffect, useState } from 'react';
import WalletTable from '../components/Wallet/WalletTable'; // Import WalletTable component
import { Wallet } from '../types/Wallet'; // Import Wallet type
import WalletService from '../api/WalletService'; // Import WalletService
import TransactionTable from "../components/Transaction/TransactionTable";
import TransactionService from "../api/TransactionService";
import {Transaction} from "../types/Transaction";
import {Loader2} from "lucide-react";
const Dashboard: React.FC = () => {
    // States for wallets
    const [wallets, setWallets] = useState<Wallet[]>([]);
    const [walletsLoading, setWalletsLoading] = useState<boolean>(false);
    const [walletsError, setWalletsError] = useState<string | null>(null);

    // States for transactions
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [transactionsLoading, setTransactionsLoading] = useState<boolean>(false);
    const [transactionsError, setTransactionsError] = useState<string | null>(null);
    useEffect(() => {
        const fetchData = async () => {
            setWalletsLoading(true);
            setTransactionsLoading(true);
            try {
                const [walletResponse, transactionResponse] = await Promise.all([
                    WalletService.getWallets(),
                    TransactionService.getTransactions(),
                ]);

                setWallets(walletResponse.data || []);
                setTransactions(transactionResponse.data || []);
            } catch (err: any) {
                setWalletsError(err.message || 'Failed to load wallets');
                setTransactionsError(err.message || 'Failed to load transactions');
            } finally {
                setWalletsLoading(false);
                setTransactionsLoading(false);
            }
        };

        fetchData();
    }, []);

    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-6">Dashboard</h2>

            <div className="space-y-6">
                {walletsLoading ? (
                    <div className="flex items-center gap-2 text-gray-600">
                        <Loader2 className="h-4 w-4 animate-spin"/>
                        <span>Loading wallets...</span>
                    </div>
                ) : walletsError ? (
                    <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                        <p className="text-red-600">{walletsError}</p>
                    </div>
                ) : wallets.length === 0 ? (
                    <p className="text-gray-600">No wallets available to display.</p>
                ) : (
                    <div>
                        <h3 className="text-xl font-semibold mb-4">Wallets</h3>
                        <WalletTable wallets={wallets}/>
                    </div>
                )}
            </div>
            <div className="space-y-6">
                {transactionsLoading ? (
                    <div className="flex items-center gap-2 text-gray-600">
                        <Loader2 className="h-4 w-4 animate-spin"/>
                        <span>Loading transactions...</span>
                    </div>
                ) : transactionsError ? (
                    <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                        <p className="text-red-600">{transactionsError}</p>
                    </div>
                ) : wallets.length === 0 ? (
                    <p className="text-gray-600">No transactions available to display.</p>
                ) : (
                    <div>
                        <h3 className="text-xl font-semibold mb-4">Transactions</h3>
                        <TransactionTable transactions={transactions}/>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Dashboard;