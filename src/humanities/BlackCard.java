package humanities;

public class BlackCard {
    public final String text;
    public final String prefix;
    public final String suffix;

    /**
     * Creates a Black Card
     * @param text string of any characters and exactly one instance of "_*"
     */
    BlackCard(String text) {
        this.text = text;
        this.prefix = text.split("_*")[0];
        this.suffix = text.split("_*")[1];
    }

    @Override
    public String toString() {
        return "<<BLACK CARD | " + text +" >>";
    }

    public String prefix() {
        return prefix;
    }

    public String suffix() {
        return suffix;
    }
}
