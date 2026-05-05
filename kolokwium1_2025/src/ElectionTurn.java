import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class ElectionTurn {

    private List<Candidate> candidates;
    private List<Vote> votes = new ArrayList<>();

    public ElectionTurn(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void populate(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine(); // pomijamy naglowek
            line = reader.readLine();        // pierwsza linia z danymi
            while (line != null) {
                Vote vote = Vote.fromCsvLine(line, candidates);
                votes.add(vote);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Blad odczytu pliku: " + e.getMessage());
        }
    }

}