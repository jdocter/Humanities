package humanities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Game implements Player.SubjectDecisionListener, Player.CzarDecisionListener {

    public static final int INACTIVE = 0;
    public static final int SUBJECTS = 1;
    public static final int CZAR = 2;
    public static final int OVER = 3;

    private int gameState = INACTIVE;
    private int round = 0;

    private final Stack<WhiteCard> whiteCards = new Stack<>();
    private final Stack<BlackCard> blackCards = new Stack<>();
    private final List<Player> players = new ArrayList<>();
    private Player czar;
    private BlackCard blackCard;
    private final Set<Player> subjects = new HashSet<>();
    private final List<Player> subjectsFinished = new ArrayList<>();

    public Game(String whiteCardsFilename, String blackCardsFilename) throws IOException {
        final BufferedReader whiteBuffer = new BufferedReader(new FileReader(new File(whiteCardsFilename)));
        final BufferedReader blackBuffer = new BufferedReader(new FileReader(new File(blackCardsFilename)));
        final List<WhiteCard> whiteCardsList = new ArrayList<>();
        final List<BlackCard> blackCardsList = new ArrayList<>();
        while (whiteBuffer.ready()) {
            whiteCardsList.add(new WhiteCard(whiteBuffer.readLine()));
        }
        while (blackBuffer.ready()) {
            blackCardsList.add(new BlackCard(blackBuffer.readLine()));
        }
        blackBuffer.close();
        whiteBuffer.close();
        Collections.shuffle(whiteCardsList);
        Collections.shuffle(blackCardsList);

        whiteCards.addAll(whiteCardsList);
        blackCards.addAll(blackCardsList);
    }

    public synchronized void addPlayer(Player player) {
        if (whiteCards.size() >= 10) {
            List<WhiteCard> hand = new ArrayList<>();
            for (int i=0; i<10; i++) {
                hand.add(whiteCards.pop());
            }
            player.onGameJoined(hand);
            notifyPlayers(player.getName() + " joined the game.");
            player.onGameOutput("You joined the game. Welcome!");
            players.add(player);
            if (gameState == INACTIVE) {
                startNewRound();
            }
        } else {
            player.onGameOutput("There are not enough cards for you to join the game");
        }
    }

    public synchronized void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            notifyPlayers(player.getName() + " left the game.");
            player.onGameOutput("You left the game");
        }
        if (gameState != INACTIVE) {
            notifyPlayers("Round terminated.");
            startNewRound();
        }
    }

    private void startCzar() {
        notifySubjectsFinished("All subjects have proposed white cards. \nYour fate now lies in "+czar.getName()+"'s hands");
        notifySubjectsFinished("Black Card: " + blackCard.toString());
        notifySubjectsFinished("Proposed White Cards: ");
        notifyCzar("All subjects have proposed white cards. \nTheir fates now lie in your hands");
        notifyCzar("Black Card: " + blackCard.toString());
        notifyCzar("Proposed White Cards: ");
        int i = 1;
        for (Player subject: subjectsFinished) {
            notifySubjectsFinished(i + ": " + subject.subjectDecision.toString());
            notifyCzar(i + ": " + subject.subjectDecision.toString());
            i++;
        }
        notifyCzar("Choose a number corresponding to a white card ");

        czar.setCzarDeciding(this, subjectsFinished);
        for (Player subject: subjectsFinished) {
            subject.setSubjectWaiting();
        }
        gameState = CZAR;
    }

    private void startNewRound() {
        reset();
        if (players.size() >= 4) {
            if (!blackCards.isEmpty()) {
                round++;
                blackCard = blackCards.pop();
                czar = players.get(round % players.size());
                subjects.addAll(players);
                subjects.remove(czar);
                notifyPlayers("________________________________________________________________________\n" +
                        "Starting round " + round );
                for (Player player : subjects) {
                    player.setSubjectDeciding(this, blackCard, czar);
                }
                czar.setCzarWaiting(blackCard);
                gameState = SUBJECTS;
            } else {
                gameOver();
            }
        } else {
            notifyPlayers("Minimum 4 players required.");
        }
    }

    private void gameOver() {
        gameState = OVER;
        notifyPlayers("Game Over.");
        for (Player player: players) {
            notifyPlayers(player.getName() + " won " + player.winnings.size() + " rounds");
        }
    }

    private void reset() {
        for (Player player: players) {
            player.reset();
        }
        gameState = INACTIVE;
        blackCard = null;
        czar = null;
        subjectsFinished.clear();
        subjects.clear();
    }

    private void notifyPlayers(String msg) {
        for (Player player: players) {
            player.onGameOutput(msg);
        }
    }

    private void notifySubjectsFinished(String msg) {
        for (Player player: subjectsFinished) {
            player.onGameOutput(msg);
        }
    }

    private void notifySubjects(String msg) {
        for (Player player: subjects) {
            player.onGameOutput(msg);
        }
    }

    private synchronized void notifyCzar(String msg) {
        if (czar != null) {
            czar.onGameOutput(msg);
        }
    }

    @Override
    public void onCzarDecision(Player czar) {
        if (this.czar == czar && subjectsFinished.contains(czar.czarDecision)) {
            czar.czarDecision.winner();
            czar.czarDecision.onGameOutput("You won the round");
            subjectsFinished.remove(czar.czarDecision);
            notifySubjectsFinished(czar.czarDecision.getName() + " won the round");
            notifyPlayers(blackCard.prefix() +  czar.czarDecision.subjectDecision.text+ blackCard.suffix());
            startNewRound();
        }
    }

    @Override
    public WhiteCard onSubjectDecision(Player subject) {
        if (subjects.contains(subject)) {
            subjects.remove(subject);
            subjectsFinished.add(subject);
            if (subjects.isEmpty()) {
                startCzar();
            }
            try {
                return whiteCards.pop();
            } catch (EmptyStackException e) {
                return null;
            }
        }
        return null;
    }
}
