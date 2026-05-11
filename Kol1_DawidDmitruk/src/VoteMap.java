import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Klasa pochodna VoivodeshipMap, która koloruje każde województwo
// na kolor przypisany kandydatowi, który wygrał tam głosowanie.
public class VoteMap extends VoivodeshipMap {

    // Mapa: nazwa województwa → zsumowane wyniki głosowania w tym województwie.
    private Map<String, Vote> wynikiVoivodeship = new HashMap<String, Vote>();

    // Mapa statyczna: imię i nazwisko kandydata → kolor przypisany mu na mapie.
    // Inicjowana raz przy ładowaniu klasy (blok static).
    private static final Map<String, String> candidateColors = new HashMap<String, String>();

    static {
        // Każdy kandydat ma unikalny kolor, żeby obszary na mapie były czytelnie rozróżnialne.
        candidateColors.put("Andrzej Sebastian DUDA",              "green");
        candidateColors.put("Rafał Kazimierz TRZASKOWSKI",         "red");
        candidateColors.put("Szymon Franciszek HOŁOWNIA",          "violet");
        candidateColors.put("Krzysztof BOSAK",                     "blue");
        candidateColors.put("Władysław Marcin KOSINIAK-KAMYSZ",    "yellow");
        candidateColors.put("Robert BIEDROŃ",                      "pink");
        candidateColors.put("Marek JAKUBIAK",                      "purple");
        candidateColors.put("Mirosław Mariusz PIOTROWSKI",         "lightblue");
        candidateColors.put("Paweł Jan TANAJNO",                   "brown");
        candidateColors.put("Waldemar Włodzimierz WITKOWSKI",      "silver");
        candidateColors.put("Stanisław Józef ŻÓŁTEK",              "gold");
    }

    // Ustawia wyniki głosowania dla poszczególnych województw.
    // Parametr wyniki: mapa nazwa_województwa → obiekt Vote z głosami.
    public void setWyniki(Map<String, Vote> wyniki) {
        this.wynikiVoivodeship = wyniki;
    }

    // Wyłania zwycięzcę w danym obiekcie Vote spośród podanej listy kandydatów.
    // Przegląda kandydatów jeden po drugim i zapamiętuje tego z największą liczbą głosów.
    private Candidate getWinner(Vote vote, List<Candidate> candidates) {
        Candidate best = null;

        for (Candidate c : candidates) {
            if (best == null) {
                best = c; // pierwszy kandydat staje się tymczasowym liderem
            } else if (vote.votes(c) > vote.votes(best)) {
                best = c; // znaleziono kandydata z większą liczbą głosów
            }
        }

        return best;
    }

    // Nadpisuje metodę z klasy bazowej:
    // dla każdego województwa wyznacza lokalnego zwycięzcę i zwraca jego kolor.
    // Jeśli brak danych lub brak koloru dla zwycięzcy – zwraca "lightgray".
    @Override
    protected String getColor(String voivodeship) {
        // Pobierz wyniki dla tego województwa.
        Vote vote = wynikiVoivodeship.get(voivodeship);

        if (vote == null) {
            // Brak danych dla tego województwa.
            return "lightgray";
        }

        List<Candidate> candidates = vote.getCandidates();
        Candidate winner = getWinner(vote, candidates);

        if (winner == null) {
            // Nie udało się wyłonić zwycięzcy (np. pusta lista kandydatów).
            return "lightgray";
        }

        // Pobierz kolor przypisany zwycięzcy.
        String color = candidateColors.get(winner.name());

        if (color == null) {
            // Kandydat nie ma przypisanego koloru (np. nieznane nazwisko).
            return "lightgray";
        }

        return color;
    }

}
