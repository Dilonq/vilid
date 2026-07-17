package djh.vilid.ideology.politics;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Ideology {
    COMMUNIST(Alignment.FARLEFT, 0xFF0000),
    ANARCHIST(Alignment.FARLEFT, 0x000000),

    PROGRESSIVE(Alignment.LEFT, 0xFFA500),
    GREEN(Alignment.LEFT, 0x00CC00),

    MODERATE(Alignment.MODERATE, 0xDDDDDD),

    LIBERTARIAN(Alignment.RIGHT, 0xFFD700),
    CONSERVATIVE(Alignment.RIGHT, 0x000080),

    FASCIST(Alignment.FARRIGHT, 0x7B3F00),
    REACTIONARY(Alignment.FARRIGHT, 0x555555);

    public final Alignment alignment;
    public final int color;

    private static final Random random = new Random();

    Ideology(Alignment alignment, int color) {
        this.alignment = alignment;
        this.color = color;
    }

    // fallback constructor
    Ideology(Alignment alignment) {
        this(alignment, 0xFFFFFF);
    }

    public static Ideology adjustIdeology(Ideology current, Ideology newIdeology, Ideology oldIdeology, int happiness) {
        if (oldIdeology == null) return current; // no reference point

        if (happiness <= 20) {
            return newIdeology.equals(oldIdeology)
                    ? current.moveAwayFrom(newIdeology)
                    : current.moveAwayFrom(oldIdeology);
        } else if (happiness >= 80) {
            return newIdeology.equals(oldIdeology)
                    ? current.moveTowards(newIdeology)
                    : current.moveTowards(oldIdeology);
        } else {
            return current; // content → no change
        }
    }


    public Ideology moveTowards(Ideology other){
        if (this.alignment.ordinal()<other.alignment.ordinal()){
            return moveRight();
        } else if (this.alignment.ordinal()>other.alignment.ordinal()){
            return moveLeft();
        }else{
            return other;
        }
    }

    public Ideology moveAwayFrom(Ideology other) { // <0 and >0 arent used to reduce teh effects of radicalization over time
        int delta = this.alignment.ordinal() - other.alignment.ordinal();
        if (delta == -1) {
            return moveLeft();
        } else if (delta == 0) {
            if (this.alignment.isRightWing()) {
                return moveRight();
            } else if (this.alignment.isLeftWing()) {
                return moveLeft();
            } else {
                return random.nextBoolean() ? moveRight() : moveLeft();
            }
        } else if (delta == 1) {
            return moveRight();
        }
        return this;
    }


    public Ideology moveLeft() {
        Ideology retIdeology = this;
        while (retIdeology.equals(this))
            retIdeology = getRandom(alignment.getLeft(1));
        return retIdeology;
    }

    public Ideology moveRight(){
        Ideology retIdeology = this;
        while (retIdeology.equals(this))
            retIdeology = getRandom(alignment.getRight(1));
        return retIdeology;
    }

    public static Ideology getRandom() {
        Ideology[] values = values();
        return values[random.nextInt(values.length)];
    }

    public static Ideology getRandom(Alignment alignment) {
        List<Ideology> filtered = Arrays.stream(values())
                .filter(i -> i.alignment == alignment)
                .toList();

        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("No ideologies with alignment: " + alignment);
        }

        return filtered.get(random.nextInt(filtered.size()));
    }
}
