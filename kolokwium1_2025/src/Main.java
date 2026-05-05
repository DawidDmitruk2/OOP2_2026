public class Main {

    public static void main(String[] args) {
        Election election = new Election();
        election.populate();

        Vote summary = Vote.summarize(election.getFirstTurn().getVotes());
        System.out.println(summary);
    }

}