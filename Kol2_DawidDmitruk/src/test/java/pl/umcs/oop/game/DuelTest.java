package pl.umcs.oop.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class DuelTest {

    @Test
    public void testIsDuelling() {
        Player player1 = new Player();
        Player player2 = new Player();
        Duel duel = new Duel(player1, player2);

        Assertions.assertTrue(player1.isDuelling());
        Assertions.assertTrue(player2.isDuelling());
    }

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