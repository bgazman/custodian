interface MfaRequest {
    token: string;
    method: string;
    state: string;
}

interface SubmitResult {
    error?: boolean;
    message?: string;
    redirectUrl?: string;
}



// Handles the MFA verification form submission
async function handleMfaSubmit(event: SubmitEvent): Promise<void> {
    event.preventDefault();

    const tokenInput = document.getElementById('mfaToken') as HTMLInputElement | null;
    if (!tokenInput) {
        showModal('MFA Token input not found');
        return;
    }
    const token = tokenInput.value;
    const isBackupMode = false; // adjust as needed
    const method = 'SMS'; // adjust as needed

    // Validate the MFA token format
    const tokenError = mfaTokenValidation(token, isBackupMode);
    if (tokenError) {
        showModal(tokenError);
        return;
    }
    const params = new URLSearchParams(window.location.search);
    const state = params.get('state');
    if (!state) {
        showModal('State parameter is missing');
        return;
    }
    const mfaRequest: MfaRequest = {
        token,
        method,
        state
    };

    const submitButton = event.submitter as HTMLButtonElement;
    try {
// Extract the state parameter from the URL query parameters
        if (!state) {
            showModal('State parameter is missing');
            return;
        }
        const result = await handleSubmit(
            '/auth/mfa/verify',
            mfaRequest,
            submitButton,
            'Verify'
        );
        if (result.error || result.message) {
            showModal(decodeURIComponent(result.message || 'Verification failed. Please try again.'));
            return;
        }
        if (result.redirectUrl) {
            window.location.href = result.redirectUrl;
        } else {
            showModal('Verification successful, but no redirect URL provided.');
        }
    } catch (error) {
        showModal(error instanceof Error ? error.message : 'An error occurred');
    }
}

function toggleBackupMode(): void {
    const tokenInput = document.getElementById('mfaToken') as HTMLInputElement;
    const isBackupMode = tokenInput.getAttribute('data-mode') === 'backup';

    if (isBackupMode) {
        tokenInput.setAttribute('pattern', '[0-9]*');
        tokenInput.setAttribute('maxlength', '6');
        tokenInput.placeholder = 'Enter verification code';
        tokenInput.removeAttribute('data-mode');
    } else {
        tokenInput.removeAttribute('pattern');
        tokenInput.setAttribute('maxlength', '10');
        tokenInput.placeholder = 'Enter backup code';
        tokenInput.setAttribute('data-mode', 'backup');
    }
    tokenInput.value = '';
}

function showModal(message: string): void {
    // Replace with your modal implementation
    alert(message);
}

function mfaTokenValidation(token: string, isBackupMode: boolean): string | null {
    if (!token) {
        return 'Token is required';
    }
    if (isBackupMode && token.length !== 10) {
        return 'Backup code must be 10 characters long';
    }
    if (!isBackupMode && token.length !== 6) {
        return 'Verification code must be 6 digits long';
    }
    return null;
}

async function handleSubmit(
    url: string,
    data: MfaRequest,
    submitButton: HTMLButtonElement,
    action: string
): Promise<SubmitResult> {
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(data),
        });
        return await response.json();
    } catch (error) {
        throw new Error('Failed to submit verification request');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const mfaForm = document.getElementById('mfaForm') as HTMLFormElement;
    if (mfaForm) {
        mfaForm.addEventListener('submit', handleMfaSubmit);
    }
    (window as any).toggleBackupMode = toggleBackupMode;
});



import React from 'react';

const Mfa: React.FC = () => {
    return (
        <form id="mfaForm">
            <div>
                <label htmlFor="mfaToken">MFA Token:</label>
                <input type="text" id="mfaToken" required />
            </div>
            <button type="submit">Verify</button>
        </form>
    );
};

export default Mfa;
