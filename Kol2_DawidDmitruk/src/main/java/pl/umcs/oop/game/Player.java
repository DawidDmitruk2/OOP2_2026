package pl.umcs.oop.game;

/**
 * Reprezentuje gracza w grze Kamień-Papier-Nożyce.
 * Przechowuje referencję do aktualnie trwającego pojedynku,
 * w którym uczestniczy gracz.
 */
public class Player {

    // Referencja do aktualnego pojedynku; null oznacza, że gracz nie pojedynkuje się
    private Duel duel;

    /**
     * Wykonuje gest w ramach aktualnego pojedynku.
     * Przekazuje gest do obiektu Duel, który zarządza logiką gry.
     */
    public void makeGesture(Gesture gesture) {
        duel.handleGesture(this, gesture);
    }

    /**
     * Przypisuje gracza do danego pojedynku.
     * Wywoływane przez konstruktor Duel przy tworzeniu nowego pojedynku.
     */
    public void enterDuel(Duel duel) {
        this.duel = duel;
    }

    /**
     * Usuwa przypisanie gracza do pojedynku po jego zakończeniu.
     * Po wywołaniu gracz może być wyzwany do nowego pojedynku.
     */
    public void leaveDuel() {
        this.duel = null;
    }

    /**
     * Sprawdza, czy gracz aktualnie uczestniczy w pojedynku.
     * Zwraca true jeśli pole duel nie jest null.
     */
    public boolean isDuelling() {
        return duel != null;
    }


}
