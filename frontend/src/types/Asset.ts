export interface Asset {
    id: string; // Unique identifier for the asset
    name: string; // Full name of the asset (e.g., 'Bitcoin', 'Ethereum')
    symbol: string; // Symbol of the asset (e.g., 'BTC', 'ETH')
    type: 'crypto' | 'stablecoin' | 'NFT'; // Type of asset
    currentPrice: number; // Current market price of the asset
    marketCap?: number; // Market capitalization (optional)
    decimals: number; // Number of decimals for the asset
    status: 'active' | 'disabled'; // Asset status (enabled/disabled)
}
