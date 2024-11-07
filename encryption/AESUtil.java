package encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AESUtil {

    // Method to generate an AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // Method to encrypt a file using AES key
    public static byte[] encryptFile(String filePath, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        return cipher.doFinal(fileBytes);
    }

    // Method to decrypt a file using AES key
    public static byte[] decryptFile(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    // Method to save encrypted data to a file
    public static void saveToFile(String filePath, byte[] data) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
    }
}
