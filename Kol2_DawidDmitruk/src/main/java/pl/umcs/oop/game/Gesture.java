package pl.umcs.oop.game;

public enum Gesture {
    ROCK, PAPER, SCISSORS;

    public static Gesture fromString(String gest) {
        if (gest.equals("r")) return ROCK;
        if (gest.equals("p")) return PAPER;
        if (gest.equals("s")) return SCISSORS;
        throw new IllegalArgumentException("Nieprawidlowy gest: " + gest);
    }

    public int compareWith(Gesture other) {
        if (other == this) return 0;
        if (other == ROCK && this == PAPER) return 1;
        if (other == PAPER && this == SCISSORS) return 1;
        if (other == SCISSORS && this == ROCK) return 1;
        return -1;
    }
}