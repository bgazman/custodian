const TransactionTable = ({ transactions }) => (
    <table className="table-auto w-full border-collapse border border-gray-200">
        <thead>
        <tr className="bg-gray-100">
            <th className="px-4 py-2 border border-gray-200">Transaction ID</th>
            <th className="px-4 py-2 border border-gray-200">Type</th>
            <th className="px-4 py-2 border border-gray-200">Asset</th>
            <th className="px-4 py-2 border border-gray-200">Amount</th>
            <th className="px-4 py-2 border border-gray-200">Date</th>
        </tr>
        </thead>
        <tbody>
        {transactions.map((tx) => (
            <tr key={tx.transactionId} className="hover:bg-gray-50">
                <td className="px-4 py-2 border border-gray-200">{tx.transactionId}</td>
                <td className="px-4 py-2 border border-gray-200">{tx.type}</td>
                <td className="px-4 py-2 border border-gray-200">{tx.asset}</td>
                <td className="px-4 py-2 border border-gray-200">{tx.amount}</td>
                <td className="px-4 py-2 border border-gray-200">{new Date(tx.date).toLocaleString()}</td>
            </tr>
        ))}
        </tbody>
    </table>
);
export default TransactionTable;