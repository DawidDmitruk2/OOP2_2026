package pl.umcs.oop.game;

public class Duel {

    private Player player1;
    private Player player2;
    private Gesture gesture1;
    private Gesture gesture2;

    public record Result(Player winner, Player loser) {}

    private Runnable onEnd;

    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    public Duel(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.enterDuel(this);
        player2.enterDuel(this);
    }

    public void handleGesture(Player player, Gesture gesture) {
        if (player == player1) {
            gesture1 = gesture;
        } else if (player == player2) {
            gesture2 = gesture;
        }
        if (gesture1 != null && gesture2 != null && onEnd != null) {
            onEnd.run();
        }
    }

    public Result evaluate() {
        Result result;

        if (gesture1.compareWith(gesture2) == 1) {
            result = new Result(player1, player2);
        } else if (gesture2.compareWith(gesture1) == 1) {
            result = new Result(player2, player1);
        } else {
            result = null;
        }

        player1.leaveDuel();
        player2.leaveDuel();
        return result;
    }
}