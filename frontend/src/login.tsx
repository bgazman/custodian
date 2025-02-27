import React, { useState, useEffect } from 'react';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [state, setState] = useState('');

  useEffect(() => {
    // Extract state from URL query parameters (if available)
    const params = new URLSearchParams(window.location.search);
    const stateParam = params.get('state');
    if (stateParam) {
      setState(stateParam);
    }

    // Log cookies to the console
    console.log('Cookies:', document.cookie);
  }, []);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const loginData = { state, email, password };

    try {
      const url = 'http://localhost:8080/auth/login';
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(loginData),
        credentials: 'include'
      });

      // If the response is a redirect, let the browser handle it
      if (response.redirected) {
        window.location.href = response.url;
      } else if (response.ok) {
        const data = await response.json();
        // For JSON responses, navigate to provided URL
        window.location.href = data.redirectUrl;
      } else {
        console.error('Login failed:', response.statusText);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email:</label>
          <input
              type="text"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">Login</button>
      </form>
  );
};

export default Login;