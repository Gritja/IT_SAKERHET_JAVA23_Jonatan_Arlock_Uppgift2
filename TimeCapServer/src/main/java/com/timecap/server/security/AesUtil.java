package com.timecap.server.security;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesUtil {

    private static final int KEY_SIZE = 256;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;

    public static Key generateKey() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[KEY_SIZE / 8];
        random.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

    public static String AesEncrypt(String message, Key key) throws Exception {
        /*Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
        */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, params);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String AesDecrypt(String encryptedMessage, Key key) throws Exception {
        /*
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
        */
        String[] parts = encryptedMessage.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        GCMParameterSpec params = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, params);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);


    }
}