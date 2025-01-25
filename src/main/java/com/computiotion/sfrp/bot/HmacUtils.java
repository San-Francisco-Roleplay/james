package com.computiotion.sfrp.bot;

import com.google.common.io.BaseEncoding;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating and validating HMACs.
 */
public class HmacUtils {
    /**
     * Validates the HMAC of the given data against the provided HMAC.
     *
     * @param data        The data to validate.
     * @param key         The secret key used for HMAC generation.
     * @param checkAgainst The HMAC to check against.
     * @return True if the generated HMAC matches the provided HMAC, false otherwise.
     * @throws InvalidKeyException If the provided key is invalid.
     */
    public static boolean validateHmac(String data, String key, String checkAgainst) throws InvalidKeyException {
        String calculatedHMAC = generateHmac(data, key);
        return calculatedHMAC.equals(checkAgainst);
    }

    /**
     * Generates an HMAC for the given data using the provided key.
     *
     * @param data The data to generate the HMAC for.
     * @param key  The secret key used for HMAC generation.
     * @return The generated HMAC as a hexadecimal string.
     * @throws InvalidKeyException If the provided key is invalid.
     */
    public static @NotNull String generateHmac(@NotNull String data, @NotNull String key) throws InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        mac.init(secretKeySpec);
        return BaseEncoding.base16().lowerCase().encode(mac.doFinal(data.getBytes()));
    }
}
