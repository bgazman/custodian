export const validation = {
    /**
     * Validates an email address.
     * @param {string} email - The email address to validate.
     * @returns {string|null} - Error message if invalid, or null if valid.
     */
    email(email) {
        if (!email?.trim()) {
            return 'Email is required';
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return 'Please enter a valid email address';
        }
        return null;
    },

    /**
     * Validates a multi-factor authentication (MFA) token.
     * @param {string} token - The MFA token to validate.
     * @param {boolean} isBackupMode - Whether the validation is for a backup code.
     * @returns {string|null} - Error message if invalid, or null if valid.
     */
    mfaToken(token, isBackupMode) {
        if (!token?.trim()) {
            return isBackupMode
                ? 'Backup code is required'
                : 'Verification code is required';
        }
        if (isBackupMode && token.length !== 10) {
            return 'Backup code must be 10 characters long.';
        }
        if (!isBackupMode && !/^\d{6}$/.test(token)) {
            return 'Verification code must be 6 digits long.';
        }
        return null;
    },

    /**
     * Validates a password.
     * @param {string} password - The password to validate.
     * @returns {string|null} - Error message if invalid, or null if valid.
     */
    password(password) {
        if (!password?.trim()) {
            return 'Password is required';
        }
        // Additional password rules can be added here if needed
        return null;
    },
};
