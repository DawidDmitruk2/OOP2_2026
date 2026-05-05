import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class Election {

    private List<Candidate> candidates = new ArrayList<>();
    private ElectionTurn firstTurn;
    private ElectionTurn secondTurn = null;

    public ElectionTurn getFirstTurn() {
        return firstTurn;
    }

    public ElectionTurn getSecondTurn() {
        return secondTurn;
    }

    public List<Candidate> getCandidates() {
        return new ArrayList<>(candidates);
    }

    private void populateCandidates(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                candidates.add(new Candidate(line));
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Blad odczytu pliku: " + e.getMessage());
        }
    }

    public void populate() {
        populateCandidates("kandydaci.txt");
        firstTurn = new ElectionTurn(candidates);
        firstTurn.populate("1.csv");
    }

}