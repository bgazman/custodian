import React, { useEffect, useState } from 'react';
import {Loader2} from "lucide-react";
const Dashboard: React.FC = () => {
    // States for wallets
    const [walletsLoading, setWalletsLoading] = useState<boolean>(false);
    const [walletsError, setWalletsError] = useState<string | null>(null);

    // States for transactions
    const [transactionsLoading, setTransactionsLoading] = useState<boolean>(false);
    const [transactionsError, setTransactionsError] = useState<string | null>(null);
    useEffect(() => {
        const fetchData = async () => {
            setWalletsLoading(true);
            setTransactionsLoading(true);
            try {
                const [walletResponse, transactionResponse] = await Promise.all([

                ]);


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

        </div>
    );
};

export default Dashboard;