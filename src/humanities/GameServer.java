package humanities;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


public class GameServer {

    /**
     * Start a GameServer for Cards Against Humanities
     *
     * Command-line: java humanities.GameServer PORT FILENAME_WHITE_CARDS FILENAME_BLACK_CARDS
     *
     * PORT is an integer that specifies the server's listening port number
     * FILENAME_WHITE_CARDS is the paths to a valid file that contain the text
     *     for white cards. Cards should be separated by newlines
     * FILENAME_BLACK_CARDS is the paths to a valid file that contain the text
     *      for black cards. Cards should be separated by newlines and each card
     *      should match the regex (~_)*_*(~_)*
     */
    public static void main(String[] args) {
        final Queue<String> arguments = new LinkedList<>(Arrays.asList(args));

        final int port;
        final Game game;

        try {
            port = Integer.parseInt(arguments.remove());
        } catch (NoSuchElementException | NumberFormatException e) {
            throw new IllegalArgumentException("missing argument PORT", e);
        }

        if (arguments.size() == 2) {
            final String whiteCardsFilename, blackCardsFilename;
            whiteCardsFilename = arguments.remove();
            blackCardsFilename = arguments.remove();
            try {
                game = new Game(whiteCardsFilename, blackCardsFilename);
                new TextServer(game, port).serve();
            } catch(IOException e) {
                throw new IllegalArgumentException("expected valid filenames");
            }
        } else {
            throw new IllegalArgumentException("expected FILENAME_WHITE_CARDS FILENAME_BLACK_CARDS");
        }
    }

}
