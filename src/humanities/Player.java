package humanities;


import java.util.*;

public class Player {
    private final static int VIEWER = 0;
    private final static int JOINED = 1;
    private final static int CZAR_WAITING = 2;
    private final static int CZAR_DECIDING = 3;
    private final static int SUBJECT_DECIDING = 4;
    private final static int SUBJECT_DECIDED = 5;
    private final static int SUBJECT_WAITING = 6;

    private final String name;
    private final GameOutputListener gameOutputListener;

    private final List<WhiteCard> hand = new ArrayList<>();
    private List<Player> subjects = new ArrayList<>();
    private BlackCard blackCard;
    private Player czar;
    private int playerState = VIEWER;
    private CzarDecisionListener czarDecisionListener;
    private SubjectDecisionListener subjectDecisionListener;

    public final Map<BlackCard,WhiteCard> winnings = new HashMap<>();
    public WhiteCard subjectDecision;
    public Player czarDecision;



    public interface GameOutputListener {
        void onGameOutput(String gameOutput);
    }

    public interface CzarDecisionListener {
        void onCzarDecision(Player czar);
    }

    public interface SubjectDecisionListener {
        WhiteCard onSubjectDecision(Player player);
    }


    Player(String name, GameOutputListener gameOutputListener) {
        this.gameOutputListener = gameOutputListener;
        this.name = name;
    }

    public void onGameJoined(List<WhiteCard> hand) {
        this.hand.addAll(hand);
        this.playerState = JOINED;
    }

    public void onGameOutput(String gameOutput) {
        gameOutputListener.onGameOutput(gameOutput);
    }

    public void reset() {
        playerState = JOINED;
        subjects.clear();
        czarDecision = null;
        subjectDecision = null;
        blackCard = null;
    }

    public void setCzarWaiting(BlackCard blackCard) {
        this.blackCard = blackCard;
        playerState = CZAR_WAITING;
        state();
    }

    public void setSubjectWaiting() {
        playerState = SUBJECT_WAITING;
    }

    public void setCzarDeciding(CzarDecisionListener czarDecisionListener, List<Player> subjects) {
        this.czarDecisionListener = czarDecisionListener;
        this.subjects = subjects;
        playerState = CZAR_DECIDING;
    }

    public void setSubjectDeciding(SubjectDecisionListener subjectDecisionListener, BlackCard blackCard, Player czar) {
        this.subjectDecisionListener = subjectDecisionListener;
        this.blackCard = blackCard;
        this.czar = czar;
        playerState = SUBJECT_DECIDING;
        state();
    }

    public void winner() {
        winnings.put(blackCard, subjectDecision);
    }

    public void integerInput(int i) {
        switch (playerState) {
            case CZAR_DECIDING:
                if (i > 0 && i < subjects.size()) {
                    czarDecision = subjects.get(i);
                    czarDecisionListener.onCzarDecision(this);
                    playerState = JOINED;
                }
                break;
            case SUBJECT_DECIDING:
                if (i > 0 && i < hand.size()) {
                    subjectDecision = hand.get(i);
                    hand.remove(i);
                    onGameOutput("You played " + subjectDecision.toString());
                    WhiteCard newWhiteCard = subjectDecisionListener.onSubjectDecision(this);
                    if (newWhiteCard != null) {
                        hand.add(newWhiteCard);
                    }
                    playerState = SUBJECT_DECIDED;
                }
                break;
            default:
                onGameOutput("Not valid input");
                break;
        }
    }

    public String getName() {
        return name;
    }

    public void seeHand() {
        onGameOutput("Your hand:");

        for (int i = 0; i < hand.size(); i++) {
            onGameOutput(i + ": " + hand.get(i));
        }
    }

    public void seeSubjects() {
        if (playerState == CZAR_DECIDING) {
            onGameOutput("Subject Proposals:");

            for (int i = 0; i < subjects.size(); i++) {
                onGameOutput(i + ": " + subjects.get(i).subjectDecision);
            }
        } else {
            onGameOutput("Invalid input");
        }
    }

    public void state() {
        switch (playerState) {
            case VIEWER:
                onGameOutput("You failed at joined the game.");
                break;
            case JOINED:
                onGameOutput("You are in the game, but there is no active round.");
                break;
            case SUBJECT_DECIDED:
                onGameOutput("You are a subject.");
                onGameOutput("You just played " + subjectDecision.toString());
                onGameOutput("The black card is " + blackCard.toString());
                onGameOutput("The Czar is "+czar.getName());
                onGameOutput("Waiting for other subjects to decide.");
                break;
            case SUBJECT_DECIDING:
                onGameOutput("You are a subject.");
                onGameOutput("The Czar, "+czar.getName()+", is waiting for you to play a white card.");
                onGameOutput("The black card is " + blackCard.toString());
                onGameOutput("Choose a number corresponding to a white card.");
                onGameOutput("Type \"hand\" to see your current hand");
                break;
            case SUBJECT_WAITING:
                onGameOutput("You are a subject.");
                onGameOutput("You just played " + subjectDecision.toString());
                onGameOutput("The black card is " + blackCard.toString());
                onGameOutput("The Czar is "+czar.getName());
                onGameOutput("Waiting for the Czar, " + czar.getName() + ", to decide.");
                break;
            case CZAR_DECIDING:
                onGameOutput("You are the Czar.");
                onGameOutput("The subjects are waiting for you to choose the best white card.");
                onGameOutput("The black card is " + blackCard.toString());
                onGameOutput("Choose a number corresponding to a white card.");
                onGameOutput("Type \"subjects\" to see your subjects' proposals.");
                break;
            case CZAR_WAITING:
                onGameOutput("You are the Czar.");
                onGameOutput("The black card is " + blackCard.toString());
                onGameOutput("Waiting for the your subjects to play a white card.");
                break;
        }
    }


}
