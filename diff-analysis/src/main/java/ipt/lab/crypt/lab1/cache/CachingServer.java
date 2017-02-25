package ipt.lab.crypt.lab1.cache;

import ipt.lab.crypt.lab1.probsource.FileDiffPropTableSource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class CachingServer {

    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {

        byte[] serializedDiffTable = Files.readAllBytes(FileDiffPropTableSource.probsFile);

        System.out.println("Array loaded, size = " + serializedDiffTable.length);

        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(PORT));

        while (true) {
            System.out.println("Waiting for connections ... ");
            Socket clientSocket = socket.accept();

            System.out.println("New client accepted");
            new Thread(new DataSender(serializedDiffTable, clientSocket)).start();
        }
    }

    private static final class DataSender implements Runnable {

        private final byte[] data;
        private final Socket clientSocket;

        public DataSender(byte[] data, Socket clientSocket) {
            this.data = data;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (OutputStream out = clientSocket.getOutputStream()) {
                out.write(data);
                clientSocket.close();

                System.out.println("Connection to " + clientSocket + " is closed");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
