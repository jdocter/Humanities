package humanities;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TextServer {

    private static final String STATE = "state";
    private static final String HAND = "hand";
    private static final String QUIT = "quit";
    private static final String SUBJECTS = "subjects";

    private final ServerSocket serverSocket;
    private final Game game;

    /**
     * Make a new text game server.
     *
     * @param port server port number
     * @throws IOException if an error occurs opening the server socket
     */
    public TextServer(Game game, int port) throws IOException {
        this.game = game;
        this.serverSocket = new ServerSocket(port);
        checkRep();
    }

    private void checkRep() {
        assert game != null;
        assert serverSocket != null;
    }
    /**
     * Run the server, listen and handle client connections
     *
     * @throws IOException
     */
    public void serve() throws IOException {
        System.err.println("Server listening on " + serverSocket.getLocalSocketAddress());

        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();

            // handle client
            new Thread(() -> {
                try {
                    handleConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Handle a single client connection.
     *
     * @param socket client socket
     * @throws IOException
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8), true);
        Player player = null;
        try {
            out.println("What is your name?");
            String name = in.readLine();
            player = new Player(name, gameOutput -> out.println(gameOutput));
            game.addPlayer(player);
            for (String input = in.readLine(); input != null; input = in.readLine()) {
                handleRequest(input, player);
            }
        } finally {
            if (player != null) game.removePlayer(player);
            out.println("closed");
            out.close();
            in.close();
        }
    }

    /**
     * Handle a single client request and return the server response.
     *
     * @param input message from client
     * @return output message to client
     */
    private void handleRequest(String input,Player player) {
        String[] tokens = input.split(" ");
        if (tokens.length == 1) {
            if (tokens[0].equals(HAND)) player.seeHand();
            else if (tokens[0].equals(STATE)) player.state();
            else if (tokens[0].equals(SUBJECTS)) player.seeSubjects();
            else if (tokens[0].equals(QUIT)) throw new RuntimeException(); // causes this exchange to close
            else {
                try {
                    player.integerInput(Integer.parseInt(tokens[0]));
                } catch (NumberFormatException e) {
                    player.onGameOutput("Invalid input");
                }
            }
        } else {
            player.onGameOutput("Invalid input");
        }


    }
}
