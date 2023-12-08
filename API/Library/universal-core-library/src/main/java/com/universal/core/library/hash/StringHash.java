package com.universal.core.library.hash;

import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class StringHash {

    public static byte[] getRandomSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    @SneakyThrows
    public static String hash(String value, String salt,int iterationCount) {
        byte[] saltByte =  Base64.getDecoder().decode(new String(salt).getBytes("UTF-8"));
        KeySpec spec = new PBEKeySpec(value.toCharArray(), saltByte, iterationCount, 512);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}
