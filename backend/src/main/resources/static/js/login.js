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

function handleResponse() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');
    const error = urlParams.get('error');
    const message = urlParams.get('message');
    const mfaRequired = urlParams.get('mfa_required');

    if (error && message) {
        showErrorModal(decodeURIComponent(message));
    } else if (mfaRequired) {
        const params = new URLSearchParams({
            email: urlParams.get('email'),
            client_id: urlParams.get('client_id'),
            redirect_uri: urlParams.get('redirect_uri'),
            state: urlParams.get('state')
        }).toString();

        window.location.href = `/mfa?${params}`;
    } else if (code && state) {
        console.log('Authorization successful. Code:', code, 'State:', state);
        // Handle successful authorization
    }
}

function validateForm(email, password) {
    if (!email || !email.trim()) {
        showErrorModal('Email is required');
        return false;
    }

    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showErrorModal('Please enter a valid email address');
        return false;
    }

    if (!password || !password.trim()) {
        showErrorModal('Password is required');
        return false;
    }

    return true;
}

function submitLogin(event) {
    event.preventDefault();  // Make sure this runs first

    const form = event.target; // This is the form element
    const submitButton = form.querySelector('button[type="submit"]');

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!validateForm(email, password)) {
        return;
    }

    const loginRequest = {
        email: email,
        password: password,
        clientId: document.getElementById('clientId').value,
        responseType: document.getElementById('responseType').value,
        redirectUri: document.getElementById('redirectUri').value,
        scope: document.getElementById('scope').value,
        state: document.getElementById('state').value,
    };

    // Show loading state
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Logging in...';

    fetch('/oauth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginRequest),
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
        console.error('Error during login:', error);
        showErrorModal('An error occurred during login. Please try again.');
        // Reset button state
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', submitLogin); // Simplified this line
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
}); // Moved this closing bracket