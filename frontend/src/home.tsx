// src/Home.tsx
import React from 'react';

const Home: React.FC = () => {
  const handleSignIn = () => {
    window.location.href = 'http://localhost:8080/oauth/authorize?response_type=code&client_id=b2aa0ed3-85fe-4d3f-92e4-8fe01ff6c080&scope=openid&state=4d833062-b61a-41cb-815a-221605864b9c&redirect_uri=http%3A%2F%2Flocalhost%3A5173%2Foauth-callback';
  };

  return (
    <div>
      <h1>Welcome to the Home Page</h1>
      <button onClick={handleSignIn}>Sign In</button>
    </div>
  );
};

export default Home;