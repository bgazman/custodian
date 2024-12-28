package consulting.gazman.security.service.impl;

import consulting.gazman.security.entity.Secret;
import consulting.gazman.security.repository.SecretRepository;
import consulting.gazman.security.service.SecretService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
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
    public Optional<Secret> findByName(String name) {
        return secretRepository.findByName(name);
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

    @Override
    public List<Secret> findAllActiveSecrets() {
        return secretRepository.findAllByActiveTrue();
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
