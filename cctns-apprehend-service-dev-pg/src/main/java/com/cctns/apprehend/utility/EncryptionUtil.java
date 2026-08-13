package com.cctns.apprehend.utility;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.exception.EncryptionFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
@Component
public class EncryptionUtil {

    @Value("${encryption.secret-key-string}")
    private String secretKeyString;

    @Value("${encryption.base64.regex}")
    private String regex;

    private SecretKey secretKey;

    private SecureRandom secureRandom;


    @PostConstruct
    public void initialize() {
        if (secretKeyString == null || secretKeyString.isEmpty()) {
            throw new EncryptionFailedException(Constants.SECRET_KEY_INIT_FAILED_EX);
        }
        secureRandom = new SecureRandom();
        try {
            // Decode Base64 key or use raw string
            byte[] keyBytes;
            if (isBase64Encoded(secretKeyString)) {
                keyBytes = Base64.getDecoder().decode(secretKeyString);

            } else {
                keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
            }

            // Ensure key is exactly 32 bytes for AES-256
            keyBytes = normalizeKeyBytes(keyBytes);
            secretKey = new SecretKeySpec(keyBytes, Constants.ENCRYPTION);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptionFailedException(Constants.SECRET_KEY_INIT_FAILED_EX);
        }
    }

    /**
     * Normalizes key bytes to exactly 32 bytes
     */
    private byte[] normalizeKeyBytes(byte[] keyBytes) {
        if (keyBytes.length > Constants.AES_KEY_SIZE) {
            // Truncate to 32 bytes
            byte[] truncated = new byte[Constants.AES_KEY_SIZE];
            System.arraycopy(keyBytes, 0, truncated, 0, Constants.AES_KEY_SIZE);
            return truncated;
        } else if (keyBytes.length < Constants.AES_KEY_SIZE) {
            // Pad to 32 bytes
            byte[] padded = new byte[Constants.AES_KEY_SIZE];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return padded;
        }
        return keyBytes;
    }

    /**
     * Encrypts data using AES-256-GCM
     * @param data Plain text to encrypt
     * @return Base64 encoded string containing IV + ciphertext + authentication tag
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            // Generate random IV
            byte[] iv = new byte[Constants.GCM_IV_LENGTH];

            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(Constants.ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(Constants.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Combine IV + ciphertext
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

            // Return as Base64
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new EncryptionFailedException(Constants.ENCRYPTION_FAILED_EX);
        }
    }

    /**
     * Decrypts data using AES-256-GCM
     * @param encryptedData Base64 encoded string containing IV + ciphertext + authentication tag
     * @return Decrypted plain text
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // Validate minimum length
            if (combined.length < Constants.GCM_IV_LENGTH + (Constants.GCM_TAG_LENGTH / 8)) {
                throw new EncryptionFailedException(Constants.DECRYPTION_FAILED_EX);
            }

            // Extract IV and ciphertext
            byte[] iv = new byte[Constants.GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - Constants.GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, Constants.GCM_IV_LENGTH);
            System.arraycopy(combined, Constants.GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(Constants.ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(Constants.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // Decrypt
            byte[] decryptedData = cipher.doFinal(ciphertext);
            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new EncryptionFailedException(Constants.DECRYPTION_FAILED_EX);
        }
    }

    /**
     * Checks if a string is Base64 encoded
     */
    public boolean isBase64Encoded(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    /**
     * Generates a secure random secret key for AES-256
     * @return Base64 encoded 256-bit key
     */
    public  String generateSecretKey() {
        byte[] keyBytes = new byte[Constants.AES_KEY_SIZE];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    /**
     * Validates if the encrypted string is properly formatted
     */
    public boolean isValidEncryptedData(String encryptedData) {
        try {
            if (!isBase64Encoded(encryptedData)) {
                return false;
            }
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return decoded.length >= Constants.GCM_IV_LENGTH + (Constants.GCM_TAG_LENGTH / 8);
        } catch (Exception e) {
            return false;
        }
    }
}
