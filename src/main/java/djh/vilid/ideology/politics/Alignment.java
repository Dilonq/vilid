package djh.vilid.ideology.politics;

public enum Alignment {
    FARLEFT,
    LEFT,
    MODERATE,
    RIGHT,
    FARRIGHT,
    ;

    public boolean isLeftWing(){
        return ordinal()<MODERATE.ordinal();
    }

    public boolean isRightWing(){
        return ordinal()> MODERATE.ordinal();
    }

    public Alignment getLeft(int distance){
        return getRight(-distance);
    }

    public Alignment getRight(int distance) {
        int ordinal = this.ordinal() + distance;

        // clamp to valid range
        if (ordinal < 0) ordinal = 0;
        if (ordinal >= Alignment.values().length) ordinal = Alignment.values().length - 1;

        return Alignment.values()[ordinal];
    }

    public boolean isMostRightWing() {
        return ordinal()==Alignment.values().length-1;
    }

    public boolean isMostLeftWing(){
        return ordinal()==0;
    }
}
