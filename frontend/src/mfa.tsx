interface MfaRequest {
    token: string;
    method: string;
    isBackupCode: boolean;
}

interface SubmitResult {
    error?: boolean;
    message?: string;
    redirectUrl?: string;
}

// async function handleMfaSubmit(event: SubmitEvent): Promise<void> {
//     event.preventDefault();
//
//     const tokenInput = document.getElementById('mfaToken') as HTMLInputElement | null;
//     if (!tokenInput) {
//         showModal('MFA Token input not found');
//         return;
//     }
//     const token = tokenInput.value;
//     const isBackupMode = false; // Set to false as per the request
//     const method = 'SMS'; // Set to 'SMS' as per the request
//
//     const tokenError = mfaTokenValidation(token, isBackupMode);
//     if (tokenError) {
//         showModal(tokenError);
//         return;
//     }
//
//     const mfaRequest: MfaRequest = {
//         token,
//         method,
//         isBackupCode: isBackupMode,
//     };
//
//     const submitButton = event.submitter as HTMLButtonElement;
//
//     try {
//         const result = await handleSubmit('/auth/mfa/verify', mfaRequest, submitButton, 'Verify');
//
//         if (result.error || result.message) {
//             showModal(decodeURIComponent(result.message || 'Verification failed. Please try again.'));
//             return;
//         }
//
//         if (result.redirectUrl) {
//             window.location.href = result.redirectUrl;
//         } else {
//             showModal('Verification successful, but no redirect URL provided.');
//         }
//     } catch (error) {
//         showModal(error instanceof Error ? error.message : 'An error occurred');
//     }
// }

// function toggleBackupMode(): void {
//     const tokenInput = document.getElementById('mfaToken') as HTMLInputElement;
//     const isBackupMode = tokenInput.getAttribute('data-mode') === 'backup';
//
//     if (isBackupMode) {
//         tokenInput.setAttribute('pattern', '[0-9]*');
//         tokenInput.setAttribute('maxlength', '6');
//         tokenInput.placeholder = 'Enter verification code';
//         tokenInput.removeAttribute('data-mode');
//     } else {
//         tokenInput.removeAttribute('pattern');
//         tokenInput.setAttribute('maxlength', '10');
//         tokenInput.placeholder = 'Enter backup code';
//         tokenInput.setAttribute('data-mode', 'backup');
//     }
//
//     tokenInput.value = '';
// }
//
// function showModal(message: string): void {
//     // Replace with actual modal implementation
//     alert(message);
// }

// function mfaTokenValidation(token: string, isBackupMode: boolean): string | null {
//     if (!token) {
//         return 'Token is required';
//     }
//     if (isBackupMode && token.length !== 10) {
//         return 'Backup code must be 10 characters long';
//     }
//     if (!isBackupMode && token.length !== 6) {
//         return 'Verification code must be 6 digits long';
//     }
//     return null;
// }

// TypeScript interfaces for MFA request and result
interface MfaRequest {
    token: string;
    method: string;
    isBackupCode: boolean;
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

    // Extract the sessionToken from the URL query parameters
    const params = new URLSearchParams(window.location.search);
    const sessionToken = params.get('sessionToken') || '';

    // Build the MFA request; in a real app, these values would come from your session context.
    const mfaRequest: MfaRequest = {
        token,
        method,
        isBackupCode: isBackupMode,
    };

    const submitButton = event.submitter as HTMLButtonElement;
    try {
        // Append the sessionToken as a query parameter
        const result = await handleSubmit(
            '/auth/mfa/verify?sessionToken=' + encodeURIComponent(sessionToken),
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
