package be.indigosolutions.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Christophe Hertigers
 * @version $Id: $
 * @created Dec 2, 2009
 */
public class MD5 {
    private static byte[] createChecksum(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMD5Checksum(String input) {
        byte[] bytes = createChecksum(input);
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}