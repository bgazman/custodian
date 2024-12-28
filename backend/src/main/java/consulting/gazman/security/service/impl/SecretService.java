package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.Secret;

import java.util.List;
import java.util.Optional;

public interface SecretService {

    /**
     * Finds a secret by its unique name.
     *
     * @param name the name of the secret.
     * @return an Optional containing the secret, if found.
     */
    Optional<Secret> findByName(String name);

    /**
     * Creates and saves a new secret.
     *
     * @param name the name of the secret.
     * @param keyType the type of the key (e.g., "RSA").
     * @return the newly created Secret.
     */
    Secret createSecret(String name, String keyType);

    /**
     * Creates and saves a client-specific signing key.
     *
     * @param clientName the name of the client.
     * @return the newly created Secret for the client.
     */
    Secret createClientSigningKey(String clientName);

    /**
     * Fetches all active secrets.
     *
     * @return a list of active secrets.
     */
    List<Secret> findAllActiveSecrets();
}
