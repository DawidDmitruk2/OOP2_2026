import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Klasa reprezentująca jeden rekord głosowania (z jednej jednostki terytorialnej)
// lub zsumowane wyniki z wielu rekordów.
public class Vote {

    // Mapa: kandydat → liczba głosów oddanych na niego w tym rekordzie.
    private Map<Candidate, Integer> votesForCandidate = new HashMap<>();

    // Lokalizacja, z której pochodzi ten głos.
    // Indeks 0 = województwo, 1 = powiat, 2 = gmina (kolejność ustawiana w fromCsvLine).
    private List<String> location = new ArrayList<>();

    // --- TWORZENIE OBIEKTU Z LINII CSV ---

    // Tworzy obiekt Vote na podstawie jednej linii pliku CSV.
    // Format linii: gmina, powiat, województwo, głosy_kand1, głosy_kand2, ...
    // Parametr candidates określa, w jakiej kolejności przypisywać kolumny głosów.
    public static Vote fromCsvLine(String line, List<Candidate> candidates) {
        String[] parts = line.split(",");
        Vote vote = new Vote();

        // Lokalizacja zapisana w odwróconej kolejności względem CSV:
        // parts[2] = województwo (poziom 0 – najszerszy)
        // parts[1] = powiat      (poziom 1)
        // parts[0] = gmina       (poziom 2 – najwęższy)
        vote.location.add(parts[2]);  //woj
        vote.location.add(parts[1]);  //pow
        vote.location.add(parts[0]);  //gmi

        // Kolumny od indeksu 3 wzwyż to liczby głosów dla kolejnych kandydatów.
        for (int i = 0; i < candidates.size(); i++) {
            int count = Integer.parseInt(parts[i + 3]);
            vote.votesForCandidate.put(candidates.get(i), count);
        }
        return vote;
    }

    // --- SUMOWANIE GŁOSÓW ---

    // Sumuje listę rekordów Vote w jeden zagregowany obiekt Vote.
    // Parametr loc przypisywany jest jako lokalizacja wynikowego obiektu
    // (np. nazwa województwa przy podsumowaniu regionalnym).
    public static Vote summarize(List<Vote> votes, List<String> loc) {
        Vote wynik = new Vote();

        for (Vote v : votes) {
            for (Candidate candidate : v.votesForCandidate.keySet()) {
                int count = v.votesForCandidate.get(candidate);

                // Pobierz dotychczasową sumę dla tego kandydata (0 jeśli jeszcze nie istnieje).
                int poprzedni = 0;
                if (wynik.votesForCandidate.containsKey(candidate)) {
                    poprzedni = wynik.votesForCandidate.get(candidate);
                }

                // Dodaj nowe głosy do sumy.
                wynik.votesForCandidate.put(candidate, poprzedni + count);
            }
        }
        wynik.location = loc;
        return wynik;
    }

    // Wersja summarize bez podawania lokalizacji – wynikowy obiekt ma pustą listę location
    // (używana do globalnego podsumowania całej tury).
    public static Vote summarize(List<Vote> votes) {
        return summarize(votes, new ArrayList<>());
    }

    // --- ODCZYT DANYCH ---

    // Zwraca liczbę głosów oddanych na konkretnego kandydata.
    public int votes(Candidate candidate) {
        int wynik = votesForCandidate.get(candidate);
        return wynik;
    }

    // Zwraca procentowy udział kandydata w łącznej liczbie głosów.
    public double percentage(Candidate candidate) {
        int total = GetSumaGlosow();
        return (votes(candidate)*100.0) / total;
    }

    // Wyświetla wyniki wszystkich kandydatów w formacie "Imię: XX.XX%".
    @Override
    public String toString() {
        String wynik = "";
        for (Candidate c : votesForCandidate.keySet()) {
            wynik = wynik + c.name() + ": " + String.format("%.2f", percentage(c)) + "%" + "\n";
        }
        return wynik;
    }

    // --- WEWNĘTRZNE OBLICZENIA ---

    // Cache dla sumy głosów – obliczana raz, przy pierwszym wywołaniu (-1 oznacza "nieobliczona").
    private int sumaGlosow = -1;

    // Zwraca łączną liczbę głosów ze wszystkich kandydatów (z cache).
    private int GetSumaGlosow() {
        if (sumaGlosow == -1) {
            int suma = 0;
            for (int v : votesForCandidate.values()) {
                suma = suma + v;
            }
            sumaGlosow = suma;
        }
        return sumaGlosow;
    }

    // --- FILTROWANIE ---

    // Zwraca tylko te rekordy Vote, których lokalizacja zaczyna się od podanego prefiksu loc.
    // Porównanie idzie poziom po poziomie: loc[0] musi pasować do v.location[0] itd.
    // Dzięki temu można filtrować np. tylko po województwie (loc.size() == 1).
    public static List<Vote> filterByLocation(List<Vote> votes, List<String> loc) {
        List<Vote> wynik = new ArrayList<>();

        for (Vote v : votes) {
            boolean match = true;
            for (int i = 0; i < loc.size(); i++) {
                if (!v.location.get(i).equals(loc.get(i))) {
                    match = false;
                }
            }
            if (match == true) {
                wynik.add(v);
            }
        }
        return wynik;
    }

    // Getter: zwraca listę lokalizacji przypisaną do tego obiektu Vote.
    public List<String> getLocation() {
        return location;
    }

    // Getter: zwraca listę wszystkich kandydatów obecnych w tym obiekcie Vote.
    public List<Candidate> getCandidates() {
        List<Candidate> list = new ArrayList<>(votesForCandidate.keySet());
        return list;
    }

}
