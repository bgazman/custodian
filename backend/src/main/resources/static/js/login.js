// Show the error modal with a custom message
function showErrorModal(message) {
    const errorModal = document.getElementById('errorModal');
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message || 'An unknown error occurred';
    errorModal.style.display = 'block';
}

// Close the error modal
function closeErrorModal() {
    const errorModal = document.getElementById('errorModal');
    errorModal.style.display = 'none';
}

function handleResponse() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');
    const error = urlParams.get('error');
    const message = urlParams.get('message');

    if (error && message) {
        // Error case
        showErrorModal(decodeURIComponent(message));
    } else if (code && state) {
        // Success case
        console.log('Authorization successful. Code:', code, 'State:', state);
        // Handle the successful authorization here
    } else {
        console.log('No recognized parameters found in URL');
    }
}

// Call handleResponse when the page loads
document.addEventListener('DOMContentLoaded', handleResponse);


// Attach event listener on page load
document.addEventListener('DOMContentLoaded', () => {
    handleResponse(); // Handle error or success parameters on page load

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', submitLogin);
    }
});



function submitLogin(event) {
    event.preventDefault();

    const loginRequest = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        clientId: document.getElementById('clientId').value,
        responseType: document.getElementById('responseType').value,
        redirectUri: document.getElementById('redirectUri').value,
        scope: document.getElementById('scope').value,
        state: document.getElementById('state').value,
    };

    fetch('/oauth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginRequest),
        redirect: 'follow'
    })
    .then(response => {
           // Get the full redirect URL
           const redirectUrl = new URL(response.url);
           const error = redirectUrl.searchParams.get('error');
           const message = redirectUrl.searchParams.get('message');

       if (error && message) {
           // Show error message in popup
           showErrorModal(decodeURIComponent(message));
           return;
       }
        window.location.href = response.url;
    })
    .catch(error => {
        console.error('Error during login:', error);
        showErrorModal(error.message);
    });
}

