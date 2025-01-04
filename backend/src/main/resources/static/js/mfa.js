import { modal } from './utils/modal.js';
import { validation } from './utils/validation.js';
import { handleSubmit } from './utils/api.js';

async function handleMfaSubmit(event) {
    event.preventDefault();

    const token = document.getElementById('mfaToken').value;
    const isBackupMode = document.getElementById('mfaToken').getAttribute('data-mode') === 'backup';
    const submitButton = event.target.querySelector('button[type="submit"]');

    const tokenError = validation.mfaToken(token, isBackupMode);
    if (tokenError) {
        modal.show(tokenError);
        return;
    }

    const mfaRequest = {
        email: document.getElementById('email').value,
        token,
        method: isBackupMode ? 'BACKUP' : document.getElementById('mfaMethod').value.toUpperCase(),
        clientId: document.getElementById('clientId').value,
        redirectUri: document.getElementById('redirectUri').value,
        state: document.getElementById('state').value,
        responseType: document.getElementById('responseType').value,
        scope: document.getElementById('scope').value,
        isBackupCode: isBackupMode,
    };

    try {
        const result = await handleSubmit('/mfa/verify', mfaRequest, submitButton, 'Verify');

        // Check for error or message in the response
        if (result.error || result.message) {
            modal.show(decodeURIComponent(result.message || 'Verification failed. Please try again.'));
            return;
        }

        // Redirect to the provided URL
        if (result.redirectUrl) {
            window.location.href = result.redirectUrl;
        } else {
            modal.show('Verification successful, but no redirect URL provided.');
        }
    } catch (error) {
        modal.show(error.message);
    }
}



function toggleBackupMode() {
    const tokenInput = document.getElementById('mfaToken');
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

    tokenInput.value = ''; // Clear input field
}


document.addEventListener('DOMContentLoaded', () => {
    const mfaForm = document.getElementById('mfaForm');
    if (mfaForm) mfaForm.addEventListener('submit', handleMfaSubmit);

    window.resendCode = resendCode;
    window.toggleBackupMode = toggleBackupMode;

    modal.init();
});


//export { resendCode, toggleBackupMode };
