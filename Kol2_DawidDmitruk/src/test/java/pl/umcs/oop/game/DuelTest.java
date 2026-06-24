package pl.umcs.oop.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Testy jednostkowe dla klasy Duel.
 * Weryfikują poprawność logiki pojedynku: przypisania graczy i wyznaczania wyniku.
 */
public class DuelTest {

    /**
     * Sprawdza, czy obaj gracze po utworzeniu Duel mają status "w trakcie pojedynku".
     * Duel w konstruktorze wywołuje enterDuel() na obu graczach,
     * więc isDuelling() powinno zwracać true dla każdego z nich.
     */
    @Test
    public void testIsDuelling() {
        Player player1 = new Player();
        Player player2 = new Player();
        Duel duel = new Duel(player1, player2);

        Assertions.assertTrue(player1.isDuelling());
        Assertions.assertTrue(player2.isDuelling());
    }

    /**
     * Sprawdza, czy wynik pojedynku jest poprawnie wyznaczany.
     * Kamień (player1) vs Nożyce (player2) – player1 powinien wygrać.
     * Po wykonaniu gestów przez obu graczy wywołuje evaluate() ręcznie
     * i weryfikuje, że Result wskazuje właściwego zwycięzcę i przegranego.
     *
     * Uwaga: onEnd nie jest ustawiony, więc evaluate() musi być wywołane jawnie.
     */
    @Test
    public void testEvaluateWinner() {
        Player player1 = new Player();
        Player player2 = new Player();
        Duel duel = new Duel(player1, player2);

        player1.makeGesture(Gesture.ROCK);
        player2.makeGesture(Gesture.SCISSORS);

        Duel.Result result = duel.evaluate();
        Assertions.assertEquals(player1, result.winner());
        Assertions.assertEquals(player2, result.loser());
    }
}
