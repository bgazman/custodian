export const buildAuthUrl = (baseUrl, clientId, redirectUri, scope = 'openid') => {
    return `${baseUrl}/oauth/authorize` +
        '?response_type=code' +
        `&client_id=${clientId}` +
        `&scope=${scope}` +
        `&state=${encodeURIComponent(crypto.randomUUID())}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}`;
};
