package humanities;

public class WhiteCard {
    public final String text;
    WhiteCard(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "<<WHITE CARD | " + text +" >>";
    }
}
