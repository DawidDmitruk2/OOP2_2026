package pl.umcs.oop.game;

/**
 * Enum reprezentujący możliwe gesty w grze Kamień-Papier-Nożyce.
 * Zawiera logikę porównywania gestów między sobą.
 */
public enum Gesture {
    ROCK, PAPER, SCISSORS;

    /**
     * Konwertuje jednoliterowy string na odpowiedni gest.
     * "r" -> ROCK, "p" -> PAPER, "s" -> SCISSORS.
     * Rzuca wyjątek, jeśli podany string nie odpowiada żadnemu gestowi.
     */
    public static Gesture fromString(String gest) {
        if (gest.equals("r")) return ROCK;
        if (gest.equals("p")) return PAPER;
        if (gest.equals("s")) return SCISSORS;
        throw new IllegalArgumentException("Nieprawidlowy gest: " + gest);
    }

    /**
     * Porównuje bieżący gest z gestem przeciwnika.
     * Zwraca:
     *   1  – bieżący gest wygrywa z gestem other (np. PAPER bije ROCK)
     *   0  – remis (oba gesty są identyczne)
     *  -1  – bieżący gest przegrywa z gestem other
     *
     * Zasady: Papier bije Kamień, Nożyce biją Papier, Kamień bije Nożyce.
     */
    public int compareWith(Gesture other) {
        if (other == this) return 0;
        if (other == ROCK && this == PAPER) return 1;
        if (other == PAPER && this == SCISSORS) return 1;
        if (other == SCISSORS && this == ROCK) return 1;
        return -1;
    }
}
