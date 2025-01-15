export const forgotPasswordModal = {
    elements: {
        modal: null,
        overlay: null,
        emailInput: null,
        form: null,
        title: null,
        description: null,
        inputs: null,
        actions: null,
        closeButton: null,
        errorMessage: null, // Element for displaying errors
    },

    isInitialized: false, // Prevent multiple initializations

    /**
     * Shows the Forgot Password modal.
     */
    show() {
        try {
                console.log('Showing Forgot Password Modal...');

            this.initElements();

            if (!this.elements.modal || !this.elements.overlay) {
                console.error('Forgot Password modal elements not found');
                return;
            }

            // Reset to default state (form view)
            this.resetToFormView();

            // Make modal and overlay visible
            this.elements.overlay.style.display = 'block';
            this.elements.modal.style.display = 'block';

            // Trigger reflow to ensure CSS transitions work
            void this.elements.modal.offsetWidth;

            this.elements.overlay.classList.add('visible');
            this.elements.modal.classList.add('visible');

            // Focus the modal for accessibility
            this.elements.modal.setAttribute('tabindex', '-1');
            this.elements.modal.focus();
        } catch (error) {
            console.error('Error showing Forgot Password modal:', error);
        }
    },

    /**
     * Closes the Forgot Password modal.
     */
    close() {
        try {
            if (!this.elements.modal || !this.elements.overlay) return;

            // Remove visible classes for transition effects
            this.elements.modal.classList.remove('visible');
            this.elements.overlay.classList.remove('visible');

            // Hide modal and overlay after the transition
            setTimeout(() => {
                this.elements.overlay.style.display = 'none';
                this.elements.modal.style.display = 'none';
            }, 300); // Match CSS transition duration
        } catch (error) {
            console.error('Error closing Forgot Password modal:', error);
        }
    },

    /**
     * Handles form submission for Forgot Password.
     * Sends the email to the backend for processing.
     * @param {Event} event - The form submission event.
     */
    async handleSubmit(event) {
        event.preventDefault();

        const email = this.elements.emailInput.value.trim();
        if (!email) {
            this.showError('Please enter a valid email address.');
            return;
        }

        try {
            const response = await fetch('/forgot-password/initiate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email }),
            });

            if (!response.ok) {
                const result = await response.json();
                throw new Error(result.message || 'An error occurred.');
            }

            // Display success message in the modal
            this.showSuccess('Password reset link sent. Check your email!');
        } catch (error) {
            console.error('Error submitting Forgot Password form:', error);
            this.showError(error.message);
        }
    },

    /**
     * Shows a success message in the modal and hides the form.
     * @param {string} message - The success message to display.
     */
    showSuccess(message) {
        this.elements.title.textContent = 'Success!';
        this.elements.description.textContent = message;

        // Hide input and form actions
        this.elements.inputs.style.display = 'none';
        this.elements.actions.style.display = 'none';
    },

    /**
     * Shows an error message in the modal without hiding the form.
     * @param {string} message - The error message to display.
     */
    showError(message) {
        if (this.elements.errorMessage) {
            this.elements.errorMessage.textContent = message;
            this.elements.errorMessage.style.display = 'block';
        }
    },

    /**
     * Resets the modal to its default (form) view.
     */
    resetToFormView() {
        this.elements.title.textContent = 'Forgot Password';
        this.elements.description.textContent = 'Enter your email address to receive a password reset link.';
        this.elements.inputs.style.display = 'block';
        this.elements.actions.style.display = 'block';

        // Clear the error message
        if (this.elements.errorMessage) {
            this.elements.errorMessage.textContent = '';
            this.elements.errorMessage.style.display = 'none';
        }
    },

    /**
     * Initializes modal elements and ensures event listeners are attached only once.
     */
    initElements() {
        if (!this.elements.modal) {
            this.elements.modal = document.getElementById('forgotPasswordModal');
        }
        if (!this.elements.overlay) {
            this.elements.overlay = document.querySelector('.modal-overlay');
        }
        if (!this.elements.emailInput) {
            this.elements.emailInput = document.getElementById('forgotPasswordEmail');
        }
        if (!this.elements.form) {
            this.elements.form = document.getElementById('forgotPasswordForm');
        }
        if (!this.elements.title) {
            this.elements.title = document.getElementById('forgotPasswordTitle');
        }
        if (!this.elements.description) {
            this.elements.description = document.getElementById('forgotPasswordDescription');
        }
        if (!this.elements.inputs) {
            this.elements.inputs = document.getElementById('forgotPasswordInputs');
        }
        if (!this.elements.actions) {
            this.elements.actions = document.getElementById('forgotPasswordActions');
        }
        if (!this.elements.errorMessage) {
            this.elements.errorMessage = document.getElementById('forgotPasswordError');
        }
        if (!this.elements.closeButton) {
            this.elements.closeButton = document.getElementById('closeForgotPasswordModal');
        }

        // Attach event listeners only once
        if (!this.isInitialized) {
            if (this.elements.closeButton) {
                this.elements.closeButton.addEventListener('click', this.close.bind(this));
            }

            if (this.elements.form) {
                this.elements.form.addEventListener('submit', this.handleSubmit.bind(this));
            }

            this.isInitialized = true;
        }
    },

    /**
     * Initializes the modal by attaching event listeners for overlay clicks and keyboard interactions.
     */
    init() {
        try {
            this.initElements();

            // Close modal on overlay click
            if (this.elements.overlay) {
                this.elements.overlay.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.close();
                });
            }

            // Handle `Escape` key press
            document.addEventListener('keydown', (e) => {
                const isModalVisible = this.elements.modal?.classList.contains('visible');
                if (isModalVisible && e.key === 'Escape') {
                    e.preventDefault();
                    this.close();
                }
            });
        } catch (error) {
            console.error('Error initializing Forgot Password modal:', error);
        }
    },
};

// Make the Forgot Password modal globally available
window.forgotPasswordModal = forgotPasswordModal;
