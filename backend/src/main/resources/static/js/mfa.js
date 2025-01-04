// Modal handling
function showErrorModal(message) {
    const errorModal = document.getElementById('errorModal');
    const modalOverlay = document.querySelector('.modal-overlay');
    const errorMessage = document.getElementById('errorMessage');

    errorMessage.textContent = message || 'An unknown error occurred';
    errorModal.style.display = 'block';
    modalOverlay.style.display = 'block';

    // Add class for animation if you want to add it later
    errorModal.classList.add('visible');
    modalOverlay.classList.add('visible');
}

function closeErrorModal() {
    const errorModal = document.getElementById('errorModal');
    const modalOverlay = document.querySelector('.modal-overlay');

    errorModal.style.display = 'none';
    modalOverlay.style.display = 'none';

    errorModal.classList.remove('visible');
    modalOverlay.classList.remove('visible');
}

function validateMfaToken(token) {
    if (!token || !token.trim()) {
        showErrorModal('Please enter a verification code');
        return false;
    }

    const tokenInput = document.getElementById('mfaToken');
    const isBackupMode = tokenInput.getAttribute('data-mode') === 'backup';

    if (!isBackupMode && !/^\d{6}$/.test(token)) {
        showErrorModal('Please enter a valid 6-digit verification code');
        return false;
    }

    return true;
}

function submitMfa(event) {
    console.log('submitMfa called'); // Add this

    event.preventDefault();  // Make sure this runs first

    const form = event.target;
    const submitButton = form.querySelector('button[type="submit"]');

    const token = document.getElementById('mfaToken').value;
    if (!validateMfaToken(token)) {
        return;
    }

    // Show loading state
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Verifying...';

const mfaElement = document.getElementById('mfaToken');
const mfaMethod = document.getElementById('mfaMethod').value;

const mfaRequest = {
    email: document.getElementById('email').value,
    token: token,
    method: mfaElement.getAttribute('data-mode') === 'backup' ? 'BACKUP' :
           mfaMethod.toLowerCase() === 'sms' ? 'SMS' : 'TOTP',
    clientId: document.getElementById('clientId').value,
    redirectUri: document.getElementById('redirectUri').value,
    state: document.getElementById('state').value,
    responseType: document.getElementById('responseType').value,
    scope: document.getElementById('scope').value,
    isBackupCode: mfaElement.getAttribute('data-mode') === 'backup'
};

    fetch('/mfa/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(mfaRequest),
        redirect: 'follow'
    })
    .then(response => {
        const redirectUrl = new URL(response.url);
        const error = redirectUrl.searchParams.get('error');
        const message = redirectUrl.searchParams.get('message');

        if (error && message) {
            showErrorModal(decodeURIComponent(message));
            // Reset button state
            submitButton.disabled = false;
            submitButton.textContent = originalButtonText;
            return;
        }
        window.location.href = response.url;
    })
    .catch(error => {
        console.error('Error during MFA verification:', error);
        showErrorModal('An error occurred during verification. Please try again.');
        // Reset button state
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

function resendCode() {
    const email = document.getElementById('email').value;
    const clientId = document.getElementById('clientId').value;

    fetch('/mfa/resend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email,
            clientId
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showErrorModal('Verification code resent successfully');
        } else {
            throw new Error(data.message || 'Failed to resend code');
        }
    })
    .catch(error => {
        showErrorModal(error.message || 'Failed to resend verification code');
    });
}

function showBackupCodeForm() {
    const tokenInput = document.getElementById('mfaToken');
    const currentToken = tokenInput.value;

    if (tokenInput.getAttribute('data-mode') === 'backup') {
        tokenInput.setAttribute('pattern', '[0-9]*');
        tokenInput.setAttribute('maxlength', '6');
        tokenInput.placeholder = 'Enter verification code';
        tokenInput.value = currentToken;
        tokenInput.removeAttribute('data-mode');
    } else {
        tokenInput.removeAttribute('pattern');
        tokenInput.setAttribute('maxlength', '10');
        tokenInput.placeholder = 'Enter backup code';
        tokenInput.value = '';
        tokenInput.setAttribute('data-mode', 'backup');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const mfaForm = document.getElementById('mfaForm');
    if (mfaForm) {
        mfaForm.addEventListener('submit', submitMfa);
    }

    // Close modal when clicking overlay
    const modalOverlay = document.querySelector('.modal-overlay');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', closeErrorModal);
    }

    // Close modal with escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeErrorModal();
        }
    });
});