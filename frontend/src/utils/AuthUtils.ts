export const buildAuthUrl = (baseUrl, clientId, redirectUri, scope = 'openid') => {
    const state = crypto.randomUUID();
    sessionStorage.setItem('oauth_state', state);

    return `${baseUrl}/oauth/authorize` +
        '?response_type=code' +
        `&client_id=${clientId}` +
        `&scope=${scope}` +
        `&state=${encodeURIComponent(state)}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}`;
};