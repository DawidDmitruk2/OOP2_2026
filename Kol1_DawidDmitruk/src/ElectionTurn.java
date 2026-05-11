import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// Reprezentuje jedną turę wyborów (pierwszą lub drugą).
// Przechowuje listę kandydatów biorących udział w tej turze
// oraz wszystkie wczytane rekordy głosowania.
public class ElectionTurn {

    // Lista kandydatów w tej turze.
    private List<Candidate> candidates;

    // Lista pojedynczych rekordów głosowania wczytanych z pliku CSV.
    private List<Vote> votes = new ArrayList<>();

    // Konstruktor przyjmuje listę kandydatów, którzy biorą udział w tej turze.
    public ElectionTurn(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    // Getter: zwraca listę kandydatów tej tury.
    public List<Candidate> getCandidates() {
        return candidates;
    }

    // Getter: zwraca wszystkie rekordy głosowania tej tury.
    public List<Vote> getVotes() {
        return votes;
    }

    // --- WCZYTYWANIE DANYCH ---

    // Wczytuje wyniki głosowania z pliku CSV o podanej ścieżce.
    // Pierwsza linia pliku to nagłówek – jest pomijana.
    // Każda kolejna linia to jeden rekord (jedna gmina).
    public void populate(String Path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Path));
            String line = reader.readLine(); // wczytaj i pomiń nagłówek
            line = reader.readLine();        // pierwsza linia z danymi

            while (line != null) {
                // Parsuj linię CSV i dodaj wynikowy obiekt Vote do listy.
                Vote v = Vote.fromCsvLine(line, candidates);
                votes.add(v);
                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Nie mozna odczytac pliku: " + e.getMessage());
        }
    }

    // --- WYŁANIANIE ZWYCIĘZCY ---

    // Sprawdza, czy któryś kandydat zdobył ponad 50% głosów.
    // Jeśli tak – zwraca go jako zwycięzcę.
    // Jeśli nie – rzuca NoWinnerException (konieczna będzie druga tura).
    public Candidate winner() throws NoWinnerException {
        Vote Glosy = Vote.summarize(votes); // zsumuj wszystkie głosy

        for (Candidate c : candidates) {
            double percent = Glosy.percentage(c);
            if (percent > 50.0) {
                return c; // znaleziono zwycięzcę
            }
        }
        throw new NoWinnerException(); // nikt nie przekroczył progu
    }

    // Zwraca dwóch kandydatów z największą liczbą głosów –
    // tych, którzy przechodzą do ewentualnej drugiej tury.
    public List<Candidate> runoffCandidates() {
        Vote glosy = Vote.summarize(votes);

        Candidate first = null;
        Candidate second = null;

        // Iteracja przez kandydatów: szukamy lidera i wicelidera.
        for (Candidate c : candidates) {
            int count = glosy.votes(c);

            if (first == null || count > glosy.votes(first)) {
                // Kandydat c jest lepszy niż dotychczasowy lider –
                // dotychczasowy lider spada na drugie miejsce.
                second = first;
                first = c;
            }
            else if (second == null || count > glosy.votes(second)) {
                // Kandydat c jest lepszy niż dotychczasowy drugi –
                // zastępuje go na drugiej pozycji.
                second = c;
            }
        }

        List<Candidate> wynik = new ArrayList<>();
        wynik.add(first);
        wynik.add(second);
        return wynik;
    }

    // --- PODSUMOWANIA ---

    // Zwraca zagregowany obiekt Vote ze wszystkich głosów tej tury (bez filtrowania).
    public Vote summarize() {
        Vote wynik = Vote.summarize(votes);
        return wynik;
    }

    // Zwraca zagregowany obiekt Vote tylko dla rekordów pasujących do podanej lokalizacji.
    // Parametr location to prefiks lokalizacji (np. ["mazowieckie"] filtruje jedno województwo).
    public Vote summarize(List<String> location) {
        List<Vote> filtered = Vote.filterByLocation(votes, location);
        Vote wynik = Vote.summarize(filtered, location);
        return wynik;
    }

}
