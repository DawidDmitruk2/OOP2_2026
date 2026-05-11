import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // --- INICJALIZACJA WYBORÓW ---

        // Utwórz obiekt wyborów i wczytaj dane:
        // kandydatów z "kandydaci.txt", głosy z "1.csv" (I tura) i "2.csv" (II tura).
        Election election = new Election();
        election.populate();

        // --- WYNIKI PIERWSZEJ TURY ---

        // Pobierz i wypisz zagregowane wyniki całej pierwszej tury.
        Vote summary1 = election.getFirstTurn().summarize();
        System.out.println("Wyniki pierwszej tury:");
        System.out.println(summary1);

        // --- WYNIKI DRUGIEJ TURY (jeśli była przeprowadzona) ---

        // secondTurn jest null, gdy ktoś wygrał już w pierwszej turze.
        if (election.getSecondTurn() != null) {
            Vote summary2 = election.getSecondTurn().summarize();
            System.out.println("Wyniki drugiej tury:");
            System.out.println(summary2);
        }

        // --- WYNIKI DRUGIEJ TURY W PODZIALE NA WOJEWÓDZTWA ---

        // Utwórz tymczasowy obiekt mapy tylko po to, żeby pobrać listę nazw województw.
        VoivodeshipMap tempMap = new VoivodeshipMap();
        List<String> voivodeships = tempMap.getVoivodeshipNames();

        // Mapa: nazwa województwa → wyniki głosowania II tury w tym województwie.
        Map<String, Vote> resultsByVoivodeship = new HashMap<>();

        for (String voivodeship : voivodeships) {
            // Filtruj głosy II tury tylko dla bieżącego województwa.
            List<String> location = new ArrayList<>();
            location.add(voivodeship); // filtr na poziomie województwa (indeks 0)

            Vote result = election.getSecondTurn().summarize(location);
            resultsByVoivodeship.put(voivodeship, result);

            System.out.println("Wyniki dla " + voivodeship + ":");
            System.out.println(result);
        }

        // --- MAPA SVG Z WYNIKAMI DRUGIEJ TURY ---

        // Utwórz mapę kolorową: każde województwo zamalowane kolorem
        // kandydata, który wygrał tam w II turze.
        VoteMap voteMap = new VoteMap();
        voteMap.setWyniki(resultsByVoivodeship);
        voteMap.saveToSvg("mapa_wyniki.svg"); // zapis do pliku SVG

        // --- MAPA SVG Z ZAZNACZONYM WOJEWÓDZTWEM ---

        // Utwórz mapę z wyróżnionym jednym województwem (lubelskie na czerwono).
        SelectableMap selectableMap = new SelectableMap();
        selectableMap.select("lubelskie");
        selectableMap.saveToSvg("mapa_zaznaczona.svg"); // zapis do pliku SVG

        // --- OGŁOSZENIE ZWYCIĘZCY ---

        // Wyświetl imię i nazwisko zwycięzcy, jeśli udało się go wyłonić.
        if (election.getWinner() != null) {
            System.out.println("Zwyciezca wyborow: " + election.getWinner().name());
        }
    }

}
