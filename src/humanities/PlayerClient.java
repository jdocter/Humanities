package humanities;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PlayerClient {
    
    /**
     * Start a PLayerClient to connect to an existing Cards Against Humanities GameServer
     * 
     * Command-line: java humanities.PlayerClient HOST PORT
     *
     * HOST is the server's hostname
     * PORT is the server's port number
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
        
        final String host;
        final int port;
        
        try {
            host = arguments.remove();
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("missing argument HOST", e);
        }
        try {
            port = Integer.parseInt(arguments.remove());
        } catch (NoSuchElementException | NumberFormatException e) {
            throw new IllegalArgumentException("missing argument PORT", e);
        }

        try (
                Socket socket = new Socket(host, port);
                BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
                PrintWriter socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8), true);
                BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
        ) {
            new Thread(() -> {
                while (!socket.isClosed()) {
                    try {
                        String message = socketIn.readLine();
                        if (message != null) {
                            System.out.println("G: " + message);
                        }
                    } catch (IOException e) {

                    }
                }
            }).start();


            while ( ! socket.isClosed()) {
                String command = systemIn.readLine();
                if (command != null) {
                    socketOut.println(command);
                }
            }
            System.out.println("connection closed");
        }
    }
}


