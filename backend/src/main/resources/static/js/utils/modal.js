
export const modal = {
    elements: {
        modal: null,
        overlay: null,
        message: null,
        closeButton: null,
        content: null, // Added to handle dynamic modal content

    },

show(message = 'An unknown error occurred') {
    try {
        this.initElements();

        if (!this.elements.modal || !this.elements.overlay || !this.elements.message) {
            console.error('Modal elements not found');
            return;
        }

        // Set message content
        this.elements.message.textContent = message;

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

        // Disable interactions with background elements
        this.disableBackground();

        // Trap focus within the modal
        this.trapFocus();
    } catch (error) {
        console.error('Error showing modal:', error);
    }
},

close() {
    try {
        this.initElements();

        if (!this.elements.modal || !this.elements.overlay) return;

        // Remove visible classes for transition effects
        this.elements.modal.classList.remove('visible');
        this.elements.overlay.classList.remove('visible');

        // Hide modal and overlay after the transition
        setTimeout(() => {
            this.elements.overlay.style.display = 'none';
            this.elements.modal.style.display = 'none';

            // Re-enable interactions with background elements
            this.enableBackground();

            // Restore focus to the first visible and enabled form element
            const activeForm = Array.from(document.forms).find((form) => {
                return form.offsetParent !== null; // Check if form is visible
            });

            if (activeForm) activeForm.focus();
        }, 300); // Match CSS transition duration
    } catch (error) {
        console.error('Error closing modal:', error);
    }
}

,

    /**
     * Initializes modal elements and event listeners if not already initialized.
     */
    initElements() {
        if (!this.elements.modal) {
            this.elements.modal = document.getElementById('errorModal');
        }
        if (!this.elements.overlay) {
            this.elements.overlay = document.querySelector('.modal-overlay');
        }
        if (!this.elements.message) {
            this.elements.message = document.getElementById('errorMessage');
        }
        if (!this.elements.closeButton && this.elements.modal) {
            this.elements.closeButton = this.elements.modal.querySelector('.modal-btn');
        }

        // Attach close button event listener
        if (this.elements.closeButton) {
            this.elements.closeButton.removeEventListener('click', this.close.bind(this));
            this.elements.closeButton.addEventListener('click', this.close.bind(this));
        }
    },

    /**
     * Initializes the modal by attaching event listeners for overlay clicks and keydown events.
     */
    init() {
        try {
            this.initElements();

            // Close modal on overlay click
            if (this.elements.overlay) {
                this.elements.overlay.removeEventListener('click', this.close.bind(this));
                this.elements.overlay.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.close();
                });
            }

            // Handle `Escape` and `Enter` key presses
            const handleKeydown = (e) => {
                const isModalVisible = this.elements.modal?.style.display === 'block';

                if (isModalVisible && e.key === 'Escape') {
                    e.preventDefault(); // Prevent default Escape behavior
                    this.close();
                }

                if (isModalVisible && e.key === 'Enter') {
                    e.preventDefault(); // Prevent form submission while modal is visible
                    this.close();
                }
            };

            // Safeguard against multiple event listener registrations
            document.removeEventListener('keydown', handleKeydown);
            document.addEventListener('keydown', handleKeydown);
        } catch (error) {
            console.error('Error initializing modal:', error);
        }
    },
};

// Make the modal globally available if needed
window.modal = modal;
