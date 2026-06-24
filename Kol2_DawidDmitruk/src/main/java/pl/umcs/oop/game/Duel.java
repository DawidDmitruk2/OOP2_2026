package pl.umcs.oop.game;

/**
 * Reprezentuje pojedynek między dwoma graczami.
 * Zarządza przyjmowaniem gestów od graczy i wyznaczaniem wyniku.
 */
public class Duel {

    // Dwaj uczestnicy pojedynku
    private Player player1;
    private Player player2;

    // Gesty wykonane przez graczy; null oznacza, że gracz jeszcze nie wykonał ruchu
    private Gesture gesture1;
    private Gesture gesture2;

    /**
     * Rekord przechowujący wynik pojedynku: referencje do zwycięzcy i przegranego.
     */
    public record Result(Player winner, Player loser) {}

    // Callback wywoływany po tym, jak obaj gracze wykonają swoje gesty
    private Runnable onEnd;

    /**
     * Ustawia akcję, która zostanie wykonana po zakończeniu pojedynku
     * (gdy obaj gracze wybiorą swoje gesty).
     */
    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    /**
     * Tworzy nowy pojedynek między dwoma graczami.
     * Automatycznie przypisuje obu graczy do tego pojedynku przez enterDuel().
     */
    public Duel(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.enterDuel(this);
        player2.enterDuel(this);
    }

    /**
     * Przyjmuje gest od gracza i zapisuje go w odpowiednim polu.
     * Gdy obaj gracze wykonali swoje gesty i ustawiony jest callback onEnd,
     * wywołuje go – co kończy pojedynek i uruchamia obliczenie wyniku.
     */
    public void handleGesture(Player player, Gesture gesture) {
        if (player == player1) {
            gesture1 = gesture;
        } else if (player == player2) {
            gesture2 = gesture;
        }
        // Pojedynek kończy się, gdy obaj gracze wybrali gest
        if (gesture1 != null && gesture2 != null && onEnd != null) {
            onEnd.run();
        }
    }

    /**
     * Oblicza wynik pojedynku na podstawie wykonanych gestów.
     * Zwraca obiekt Result z referencjami do zwycięzcy i przegranego,
     * lub null w przypadku remisu.
     * Na końcu zwalnia obu graczy z pojedynku przez leaveDuel().
     */
    public Result evaluate() {
        Result result;

        if (gesture1.compareWith(gesture2) == 1) {
            // Player1 wygrywa
            result = new Result(player1, player2);
        } else if (gesture2.compareWith(gesture1) == 1) {
            // Player2 wygrywa
            result = new Result(player2, player1);
        } else {
            // Remis
            result = null;
        }

        // Gracze opuszczają pojedynek i mogą teraz wziąć udział w kolejnym
        player1.leaveDuel();
        player2.leaveDuel();
        return result;
    }
}
