package network;

import encryption.RSAUtil;
import encryption.AESUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;

public class FileReceiver {

    // Method to decrypt AES key using RSA private key
    public static SecretKey decryptAESKey(byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        byte[] aesKeyBytes = RSAUtil.decryptRSA(encryptedKey, privateKey);
        return new SecretKeySpec(aesKeyBytes, "AES");
    }

    // Method to receive file, decrypt AES key, and save decrypted file
    public static void receiveFile(int port, String savePath, PrivateKey privateKey) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for a connection on port " + port + "...");
            try (Socket socket = serverSocket.accept();
                 InputStream in = socket.getInputStream()) {

                System.out.println("Connection established.");

                // Read the size of the encrypted AES key
                DataInputStream dataInputStream = new DataInputStream(in);
                int keySize = dataInputStream.readInt(); // Read the size of the AES key
                byte[] encryptedAESKey = new byte[keySize];
                dataInputStream.readFully(encryptedAESKey); // Read the entire key

                // Decrypt AES key with RSA private key
                SecretKey aesKey = decryptAESKey(encryptedAESKey, privateKey);

                // Read the size of the encrypted file data
                int fileSize = dataInputStream.readInt(); // Read the size of the file
                byte[] encryptedFileData = new byte[fileSize];
                dataInputStream.readFully(encryptedFileData); // Read the entire file data

                // Decrypt the file with AES key
                byte[] decryptedFileData = AESUtil.decryptFile(encryptedFileData, aesKey);

                // Save the decrypted file
                Files.write(Paths.get(savePath), decryptedFileData);
                System.out.println("File received and decrypted successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
