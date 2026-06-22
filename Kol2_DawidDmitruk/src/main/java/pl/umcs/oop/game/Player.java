package pl.umcs.oop.game;

public class Player {

    private Duel duel;

    public void makeGesture(Gesture gesture) {
        duel.handleGesture(this, gesture);
    }

    public void enterDuel(Duel duel) {
        this.duel = duel;
    }

    public void leaveDuel() {
        this.duel = null;
    }

    public boolean isDuelling() {
        return duel != null;
    }


}