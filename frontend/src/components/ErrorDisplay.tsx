import React from "react";

interface ErrorDisplayProps {
    error: string | null;
}

const ErrorDisplay: React.FC<ErrorDisplayProps> = ({ error }) => {
    if (!error) return null;

    return (
        <div className="mb-4 text-red-600 bg-red-100 border border-red-400 p-2 rounded">
            {error.split("\n").map((err, index) => (
                <div key={index}>{err}</div>
            ))}
        </div>
    );
};

export default ErrorDisplay;
