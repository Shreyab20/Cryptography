import encryption.KeyPairGeneratorUtil;
import encryption.AESUtil;
import encryption.RSAUtil;
import network.FileSender;
import network.FileReceiver;

import javax.crypto.SecretKey;
import java.security.KeyPair;

public class Main {
    public static void main(String[] args) {
        try {
            // Generate RSA key pair
            KeyPair rsaKeyPair = KeyPairGeneratorUtil.generateRSAKeyPair();

            // Generate AES key
            SecretKey aesKey = AESUtil.generateAESKey();

            // Encrypt the file with AES key
            String originalFilePath = "C:\\Users\\Shreya\\Desktop\\Desktop\\Projects\\Cryptography\\sample.txt"; 
            byte[] encryptedFileData = AESUtil.encryptFile(originalFilePath, aesKey);
            AESUtil.saveToFile("encryptedFile.dat", encryptedFileData);

            // Encrypt AES key with RSA public key
            byte[] encryptedAESKey = RSAUtil.encryptAESKey(aesKey, rsaKeyPair.getPublic());

            // Start the FileReceiver in a separate thread
            new Thread(() -> {
                try {
                    // Specify the path to save the decrypted file
                    String savePath = "decryptedFile.dat";
                    FileReceiver.receiveFile(8080, savePath, rsaKeyPair.getPrivate());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Give some time for the receiver to start
            Thread.sleep(1000); // Adjust the sleep time if necessary

            // Send encrypted file and AES key over network
            String host = "localhost";
            int port = 8080;
            FileSender.sendFile(host, port, "encryptedFile.dat", encryptedAESKey);

            System.out.println("File encrypted, sent, received, and decrypted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
