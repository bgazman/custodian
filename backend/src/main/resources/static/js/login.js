import { modal } from './utils/modal.js';
import { forgotPasswordModal } from './utils/forgot-password_modal.js';
import { validation } from './utils/validation.js';
import { handleSubmit } from './utils/api.js';

function validateForm(email, password) {
    const emailError = validation.email(email);
    if (emailError) {
        modal.show(emailError);
        return false;
    }

    const passwordError = validation.password(password);
    if (passwordError) {
        modal.show(passwordError);
        return false;
    }
    return true;
}

async function submitLogin(event) {
    event.preventDefault();

    console.log('Form submission triggered'); // Debugging log

    const form = event.target;
    const submitButton = form.querySelector('button[type="submit"]');
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!validateForm(email, password)) return;

    const loginRequest = {
        email,
        password,
        clientId: document.getElementById('clientId').value,
        responseType: document.getElementById('responseType').value,
        redirectUri: document.getElementById('redirectUri').value,
        scope: document.getElementById('scope').value,
        state: document.getElementById('state').value,
    };

    try {
        const result = await handleSubmit('/oauth/login', loginRequest, submitButton, 'Login');

        if (result.error || result.message) {
            modal.show(decodeURIComponent(result.message || 'An error occurred. Please try again.'));
            return;
        }

        if (result.redirectUrl) {
            window.location.href = result.redirectUrl;
        } else {
            modal.show('Login successful, but no redirect URL provided.');
        }
    } catch (error) {
        modal.show(error.message);
    }

function showForgotPasswordModal() {
    modal.show('forgotPasswordModal'); // Opens the Forgot Password modal
}

function closeForgotPasswordModal() {
    modal.close(); // Closes the currently active modal
}

}



// Attach event listeners on DOMContentLoaded
document.addEventListener('DOMContentLoaded', () => {
    // Login form submission
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', submitLogin);
    }

    // Forgot Password link click
    const forgotPasswordLink = document.getElementById('forgotPasswordLink');
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', (event) => {
            event.preventDefault();
            forgotPasswordModal.show(); // Opens the Forgot Password modal
        });
    }

    // Initialize modals
    modal.init(); // Initialize the generic modal
    forgotPasswordModal.init(); // Initialize the Forgot Password modal
});