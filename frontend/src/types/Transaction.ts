export interface Transaction {
    id: string; // Unique identifier for the transaction
    type: 'deposit' | 'withdrawal' | 'transfer'; // Transaction type
    asset: string; // Asset type (e.g., 'BTC', 'ETH', 'USDT')
    amount: number; // Transaction amount
    currency: string; // Currency involved (e.g., 'USD', 'EUR')
    sender: string; // Sender wallet or account ID
    recipient: string; // Recipient wallet or account ID
    fee: number; // Fee applied to the transaction
    status: 'pending' | 'completed' | 'failed'; // Transaction status
    blockchainTxId?: string; // Blockchain-specific transaction ID (optional)
    timestamp: Date; // Timestamp when the transaction was initiated
}
