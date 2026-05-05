import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vote {

    private Map<Candidate, Integer> votesForCandidate = new HashMap<>();
    private List<String> location = new ArrayList<>();

    public static Vote fromCsvLine(String line, List<Candidate> candidates) {
        Vote vote = new Vote();

        String[] parts = line.split(",");

        vote.location.add(parts[2]); // wojewodztwo
        vote.location.add(parts[1]); // powiat
        vote.location.add(parts[0]); // gmina

        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            int votes = Integer.parseInt(parts[i + 3]);
            vote.votesForCandidate.put(candidate, votes);
        }

        return vote;
    }

    public static Vote summarize(List<Vote> votes) {
        Vote result = new Vote();

        for (Vote vote : votes) {
            for (Candidate candidate : vote.votesForCandidate.keySet()) {
                int currentVotes = vote.votesForCandidate.get(candidate);

                if (result.votesForCandidate.containsKey(candidate)) {
                    int previousVotes = result.votesForCandidate.get(candidate);
                    result.votesForCandidate.put(candidate, previousVotes + currentVotes);
                } else {
                    result.votesForCandidate.put(candidate, currentVotes);
                }
            }
        }

        return result;
    }

    public int votes(Candidate candidate) {
        return votesForCandidate.get(candidate);
    }

    public double percentage(Candidate candidate) {
        int totalVotes = 0;
        for (int v : votesForCandidate.values()) {
            totalVotes = totalVotes + v;
        }
        return (votes(candidate) * 100.0) / totalVotes;
    }

    @Override
    public String toString() {
        String result = "";
        for (Candidate candidate : votesForCandidate.keySet()) {
            double percent = percentage(candidate);
            result = result + candidate.name() + ": " + percent + "%\n";
        }
        return result;
    }

}