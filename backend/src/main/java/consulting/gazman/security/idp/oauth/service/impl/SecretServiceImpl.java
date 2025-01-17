package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.idp.oauth.entity.Secret;
import consulting.gazman.security.idp.oauth.repository.SecretRepository;
import consulting.gazman.security.idp.oauth.service.SecretService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SecretServiceImpl implements SecretService {

    private final SecretRepository secretRepository;

    public SecretServiceImpl(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    @Override
    public List<Secret> findAll() {
        return secretRepository.findAll();
    }

    @Override
    public Optional<Secret> findById(Long id) {
        return secretRepository.findById(id);
    }

    @Override
    public Optional<Secret> findByName(String name) {
        return secretRepository.findByName(name);
    }

    @Override
    public Secret save(Secret secret) {
        if (secret.getId() == null) {
            secret.setCreatedAt(LocalDateTime.now());
            secret.setLastRotatedAt(LocalDateTime.now());
            secret.setActive(true);
        }
        secret.setUpdatedAt(LocalDateTime.now());
        return secretRepository.save(secret);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Secret> secret = findById(id);
        if (secret.isPresent()) {
            Secret existingSecret = secret.get();
            existingSecret.setActive(false);
            existingSecret.setUpdatedAt(LocalDateTime.now());
            secretRepository.save(existingSecret);
        }
    }

    @Override
    public List<Secret> findAllActiveSecrets() {
        return secretRepository.findByActiveTrue();
    }
    @Override
    public Secret createSecret(String name, String keyType) {
        try {
            // Generate a new RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyType);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Convert keys to PEM format
            String publicKeyPem = encodePublicKeyToPem(keyPair.getPublic());
            String privateKeyPem = encodePrivateKeyToPem(keyPair.getPrivate());

            // Create and save the secret
            Secret secret = new Secret();
            secret.setName(name);
            secret.setType(keyType);
            secret.setPublicKey(publicKeyPem);
            secret.setPrivateKey(privateKeyPem);
            secret.setActive(true);
            return secretRepository.save(secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }

    @Override
    public Secret createClientSigningKey(String clientName) {
        String keyName = clientName + "-signing-key";
        return createSecret(keyName, "RSA");
    }


    private String encodePublicKeyToPem(PublicKey publicKey) {
        try {
            return "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getEncoder().encodeToString(publicKey.getEncoded()) +
                    "\n-----END PUBLIC KEY-----";
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode public key to PEM", e);
        }
    }

    private String encodePrivateKeyToPem(PrivateKey privateKey) {
        try {
            return "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getEncoder().encodeToString(privateKey.getEncoded()) +
                    "\n-----END PRIVATE KEY-----";
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode private key to PEM", e);
        }
    }
}
