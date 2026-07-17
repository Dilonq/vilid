package djh.vilid.ideology.politics;

public class Party {
    private final String name;
    private final Ideology ideology;
    private int prestige;

    public Party(String name, Ideology ideology, int prestige) {
        this.name = name;
        this.ideology = ideology;
        this.prestige = prestige;
    }

    public String getName() {
        return name;
    }

    public Ideology getIdeology() {
        return ideology;
    }

    public int getPrestige() {
        return prestige;
    }
    public void deltaPrestige(int delta){prestige+=delta;}
}
