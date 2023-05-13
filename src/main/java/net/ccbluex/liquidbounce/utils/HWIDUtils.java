package net.ccbluex.liquidbounce.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ChengFeng
 * @since 2022/11/30
 */
public class HWIDUtils {
    public static String getHWID() {
        try {
            StringBuilder s = new StringBuilder();
            String main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME");
            byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            byte[] sha = messageDigest.digest(bytes);
            int i = 0;
            for (byte b : sha) {
                s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
                if (i != sha.length - 1) {
                    s.append("-");
                }
                i++;
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
