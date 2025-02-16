import React, { useState, useEffect } from 'react';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  useEffect(() => {
    // Extract sessionToken from URL query parameters (if available)
    const params = new URLSearchParams(window.location.search);

    // Log cookies to the console
    console.log('Cookies:', document.cookie);
  }, []);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const loginData = { email, password };

    try {
      // Pass the session token as a query parameter in the request URL
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

      if (response.ok) {
        const data = await response.json();
        // Navigate to the redirect URL provided by the server
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