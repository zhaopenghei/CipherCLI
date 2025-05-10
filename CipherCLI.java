import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class CipherCLI {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("用法: java CipherCLI encrypt|decrypt "文本" 密码");
            return;
        }

        String mode = args[0];
        String text = args[1];
        String password = args[2];

        try {
            if (mode.equalsIgnoreCase("encrypt")) {
                String result = encrypt(text, password);
                System.out.println("加密结果: " + result);
            } else if (mode.equalsIgnoreCase("decrypt")) {
                String result = decrypt(text, password);
                System.out.println("解密结果: " + result);
            } else {
                System.out.println("不支持的模式: " + mode);
            }
        } catch (Exception e) {
            System.out.println("出错: " + e.getMessage());
        }
    }

    private static String encrypt(String text, String password) throws Exception {
        byte[] key = getKey(password);
        byte[] iv = new byte[16]; // 默认0填充的IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static String decrypt(String encryptedBase64, String password) throws Exception {
        byte[] key = getKey(password);
        byte[] iv = new byte[16]; // 默认0填充的IV
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, "UTF-8");
    }

    private static byte[] getKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        return sha.digest(password.getBytes("UTF-8"));
    }
}
