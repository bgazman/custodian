import React, { useState, useEffect } from 'react';

const Consent: React.FC = () => {
    const [clientName, setClientName] = useState('');
    const [scopes, setScopes] = useState<string[]>([]);
    const [selectedScopes, setSelectedScopes] = useState<string[]>([]);
    const [state, setState] = useState('');

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const stateParam = params.get('state');
        if (stateParam) {
            setState(stateParam);
            fetchConsentData(stateParam);
        }
    }, []);

    const fetchConsentData = async (stateParam: string) => {
        try {
            const response = await fetch(`http://localhost:8080/oauth/consent?state=${stateParam}`, {
                method: 'GET',  // Explicitly set method to GET
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                setClientName(data.clientName);
                setScopes(data.scopes);
                setSelectedScopes(data.scopes);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            const response = await fetch('/oauth/consent-approve', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ state, approvedScopes: selectedScopes }),
                credentials: 'include'
            });
            if (response.ok) {
                const data = await response.json();
                window.location.href = data.redirectUrl;
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div>
            <h2>Consent Required</h2>
            <p>Application <strong>{clientName}</strong> is requesting access to:</p>
            <form onSubmit={handleSubmit}>
                {scopes.map(scope => (
                    <div key={scope}>
                        <input
                            type="checkbox"
                            id={scope}
                            checked={selectedScopes.includes(scope)}
                            onChange={(e) => {
                                if (e.target.checked) {
                                    setSelectedScopes([...selectedScopes, scope]);
                                } else {
                                    setSelectedScopes(selectedScopes.filter(s => s !== scope));
                                }
                            }}
                        />
                        <label htmlFor={scope}>{scope}</label>
                    </div>
                ))}
                <button type="submit">Approve</button>
                <button type="button" onClick={() => window.history.back()}>Deny</button>
            </form>
        </div>
    );
};

export default Consent;