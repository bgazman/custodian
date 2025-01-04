import { modal } from './utils/modal.js';
import { validation } from './utils/validation.js';
import { handleSubmit } from './utils/api.js';

function validateForm(resetToken, newPassword, confirmPassword) {
    const passwordError = validation.password(newPassword);
    if (passwordError) {
        modal.show(passwordError);
        return false;
    }

    const matchError = validation.passwordMatch(newPassword, confirmPassword);
    if (matchError) {
        modal.show(matchError);
        return false;
    }

    return true;
}

async function submitPasswordReset(event) {
    event.preventDefault();

    console.log('Password reset form submission triggered'); // Debugging log

    const form = event.target;
    const submitButton = form.querySelector('button[type="submit"]');
    const email = document.getElementById('email').value;
    const resetToken = document.getElementById('resetToken').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!validateForm(resetToken, newPassword, confirmPassword)) return;

    const passwordResetRequest = {
        email,
        token: resetToken,
        newPassword,
    };

    try {
        const result = await handleSubmit('/forgot-password/reset', passwordResetRequest, submitButton, 'Reset Password');

        if (result.error || result.message) {
            modal.show(decodeURIComponent(result.message || 'An error occurred. Please try again.'));
            return;
        }

        if (result.redirectUrl) {
            window.location.href = result.redirectUrl;
        } else {
            modal.show('Password reset successful.');
            form.reset(); // Optionally clear form fields
        }
    } catch (error) {
        modal.show(error.message);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const passwordResetForm = document.getElementById('passwordResetForm');
    if (passwordResetForm) {
        passwordResetForm.addEventListener('submit', submitPasswordReset);
    }
    modal.init();
});
