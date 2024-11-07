package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileSender {
    public static void sendFile(String host, int port, String encryptedFilePath, byte[] encryptedAESKey) throws Exception {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             FileInputStream fileIn = new FileInputStream(encryptedFilePath)) {

            // Send the size of the encrypted AES key
            out.write(intToByteArray(encryptedAESKey.length));
            out.write(encryptedAESKey); // Send the actual AES key

            // Send the size of the encrypted file data
            File file = new File(encryptedFilePath);  // Ensure this line works correctly
            out.write(intToByteArray((int) file.length())); // Send file size

            // Send the encrypted file data
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully.");
        }
    }

    // Helper method to convert an integer to a byte array
    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }
}
