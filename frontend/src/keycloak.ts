import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
    url: 'http://localhost:9090/auth',
    realm: 'crypto-custodian',
    clientId: 'crypto-custodian-ui',
});

keycloak.init({
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
    pkceMethod: 'S256',
});