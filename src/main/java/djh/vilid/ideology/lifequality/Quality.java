package djh.vilid.ideology.lifequality;

public enum Quality {
    EXCELLENT,
    GREAT,
    GOOD,
    FAIR,
    MEDIOCRE,
    POOR,
    BAD,
    HORRIBLE;

    public boolean isBetterThan(Quality other) {
        return this.ordinal() < other.ordinal();
    }
}
