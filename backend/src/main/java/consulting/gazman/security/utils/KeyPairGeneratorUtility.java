package consulting.gazman.security.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyPairGeneratorUtility {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        // Convert keys to PEM format
        String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key: " + publicKey);
    }
}
