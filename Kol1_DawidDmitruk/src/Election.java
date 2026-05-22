import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// Główna klasa reprezentująca całe wybory prezydenckie.
// Zarządza kandydatami, turami głosowania i wyłanianiem zwycięzcy.
public class Election {

    // Lista wszystkich kandydatów wczytanych z pliku.
    private List<Candidate> candidates = new ArrayList<>();

    // Pierwsza tura jest zawsze przeprowadzana.
    private ElectionTurn firstTurn;

    // Druga tura istnieje tylko wtedy, gdy w pierwszej nikt nie przekroczył 50%.
    private ElectionTurn secondTurn = null;

    // Zwycięzca wyborów (null, jeśli jeszcze nie wyłoniono lub remis w II turze).
    private Candidate winner = null;

    // Getter: zwraca obiekt pierwszej tury.
    public ElectionTurn getFirstTurn() {
        return firstTurn;
    }

    // Getter: zwraca obiekt drugiej tury (lub null, jeśli była niepotrzebna).
    public ElectionTurn getSecondTurn() {
        return secondTurn;
    }

    // Getter: zwraca zwycięzcę wyborów (lub null, jeśli brak).
    public Candidate getWinner() {
        return winner;
    }

    // Getter: zwraca kopię listy kandydatów (zabezpiecza przed modyfikacją z zewnątrz).
    //plytka kopia
    public List<Candidate> getCandidates() {
        return new ArrayList<>(candidates);
    }

    // --- WCZYTYWANIE KANDYDATÓW ---

    // Wczytuje listę kandydatów z pliku tekstowego – każda linia to jeden kandydat.
    private void populateCandidates(String Path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Path));
            String line = reader.readLine();
            while (line != null) {
                candidates.add(new Candidate(line)); // jedna linia = jeden kandydat
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Blad wczytywania kandydatow: " + e.getMessage());
        }
    }

    // --- GŁÓWNA METODA INICJUJĄCA ---

    // Wczytuje dane i przeprowadza logikę wyborów:
    // 1. Wczytuje kandydatów z pliku "kandydaci.txt".
    // 2. Przeprowadza pierwszą turę na danych z "1.csv".
    // 3. Jeśli w I turze nie ma zwycięzcy, przeprowadza drugą turę na danych z "2.csv".
    public void populate() {
        populateCandidates("kandydaci.txt");

        // Utwórz i wczytaj pierwszą turę ze wszystkimi kandydatami.
        firstTurn = new ElectionTurn(candidates);
        firstTurn.populate("1.csv");

        try {
            // Próba wyłonienia zwycięzcy z I tury.
            winner = firstTurn.winner();
        } catch (NoWinnerException e) {
            // Nikt nie przekroczył 50% – konieczna II tura.
            // Wyznaczamy dwóch kandydatów z najlepszymi wynikami.
            List<Candidate> finalisci = firstTurn.runoffCandidates();

            // Utwórz i wczytaj drugą turę tylko z dwoma finalistami.
            secondTurn = new ElectionTurn(finalisci);
            secondTurn.populate("2.csv");

            try {
                // Próba wyłonienia zwycięzcy z II tury.
                winner = secondTurn.winner();
            } catch (NoWinnerException e2) {
                // Remis również w drugiej turze – sytuacja wyjątkowa.
                System.out.println("Brak zwyciezcy rowniez w drugiej turze");
            }
        }
    }

}
