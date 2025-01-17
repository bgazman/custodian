package consulting.gazman.security.idp.oauth.service;

import consulting.gazman.security.idp.oauth.entity.Secret;

import java.util.List;
import java.util.Optional;

public interface SecretService {

    List<Secret> findAll();

    Optional<Secret> findById(Long id);
    Optional<Secret> findByName(String name);

    Secret save(Secret secret);

    void deleteById(Long id);

    Secret createSecret(String name, String keyType);


    Secret createClientSigningKey(String clientName);


    List<Secret> findAllActiveSecrets();
}
