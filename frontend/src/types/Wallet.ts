export interface Wallet {
    id: string; // Unique identifier for the wallet
    address: string; // Blockchain address
    type: 'hot' | 'cold' | 'hardware'; // Type of wallet
    currency: string; // Currency (e.g., 'BTC', 'ETH')
    balance: number; // Wallet balance
    status: 'active' | 'archived' | 'locked'; // Wallet status
    createdAt: Date; // Timestamp of wallet creation
    updatedAt?: Date; // Timestamp of last balance update (optional)
}
