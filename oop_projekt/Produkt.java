import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TEMAT: ABSTRAKCJA + ENKAPSULACJA + KONSTRUKTORY
 *
 * Klasa abstrakcyjna - nie można jej bezpośrednio instancjonować (new Produkt() = błąd).
 * Służy jako "szablon" dla klas potomnych.
 *
 * Na kolokwium często pojawia się wzorzec:
 *   - klasa abstrakcyjna z prywatnymi polami i publicznymi akcesorami
 *   - statyczna metoda fabryczna fromCsv()
 *   - metoda abstrakcyjna do nadpisania przez podklasy
 */
public abstract class Produkt {

    // ENKAPSULACJA: pole prywatne - dostęp tylko przez akcesor
    // 'final' = wartość ustawiana raz w konstruktorze, potem niezmienna
    private final String nazwa;

    // Prywatna statyczna lista produktów (współdzielona przez wszystkie obiekty)
    // KONTENERY: List<Produkt> przechowuje polimorficzne obiekty
    private static List<Produkt> wszystkieProdukty = new ArrayList<>();

    /**
     * KONSTRUKTOR: ustawia pola klasy.
     * Konstruktor klasy abstrakcyjnej może być wywołany tylko przez podklasy (super(...)).
     */
    public Produkt(String nazwa) {
        this.nazwa = nazwa;
    }

    /**
     * AKCESOR (getter): publiczny dostęp do prywatnego pola.
     * Konwencja: getNazwaPola()
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * METODA ABSTRAKCYJNA: brak ciała (implementacji).
     * Każda konkretna podklasa MUSI ją nadpisać (@Override).
     * Tutaj: każdy produkt liczy cenę inaczej.
     */
    public abstract double getCena(int rok, int miesiac);

    /**
     * METODA ABSTRAKCYJNA: zwraca opis produktu.
     * Wymusza, żeby podklasy zdefiniowały własną wersję.
     */
    public abstract String getOpis();

    // =========================================================
    //   STATYCZNE METODY - operują na liście wszystkichProduktów
    // =========================================================

    /**
     * PLIKI + WYJĄTKI: statyczna metoda fabryczna.
     * Wzorzec "fromCsv" pojawia się na każdym kolokwium.
     *
     * @param konstruktor - obiekt funkcyjny (np. ProduktSpoż::fromCsv lub ProduktPrzemysłowy::fromCsv)
     *                      Typ: Function<Path, Produkt> - przyjmuje Path, zwraca Produkt
     * @param katalog     - ścieżka do katalogu z plikami CSV
     */
    public static void dodajProdukty(Function<Path, Produkt> konstruktor, Path katalog) throws IOException {
        // Files.list() - PROGRAMOWANIE FUNKCYJNE: stream plików w katalogu
        Files.list(katalog)
             .filter(p -> p.toString().endsWith(".csv"))  // lambda filtrująca
             .forEach(sciezka -> {
                 Produkt p = konstruktor.apply(sciezka);  // wywołanie obiektu funkcyjnego
                 wszystkieProdukty.add(p);
             });
    }

    /**
     * Czyści listę produktów (przydatne w testach).
     */
    public static void wyczyscProdukty() {
        wszystkieProdukty.clear();
    }

    /**
     * PLIKI + WYJĄTKI + PROGRAMOWANIE FUNKCYJNE:
     * Wyszukuje produkt po prefiksie nazwy.
     *
     * Rzuca checked exceptions - kompilator wymusza ich przechwycenie.
     *
     * @param prefiks - początek nazwy produktu
     * @return znaleziony produkt
     * @throws NiejednoznacznyProduktException gdy pasuje więcej niż 1
     * @throws IndexOutOfBoundsException gdy nie pasuje żaden
     */
    public static Produkt getProdukt(String prefiks)
            throws NiejednoznacznyProduktException, IndexOutOfBoundsException {

        // STREAM API: filtrowanie listy za pomocą lambdy
        List<Produkt> pasujace = wszystkieProdukty.stream()
                .filter(p -> p.getNazwa().startsWith(prefiks))
                .collect(Collectors.toList());

        if (pasujace.isEmpty()) {
            // Rzucamy standardowy wyjątek z komunikatem
            throw new IndexOutOfBoundsException("Brak produktu z prefiksem: " + prefiks);
        } else if (pasujace.size() > 1) {
            // Rzucamy własny wyjątek, przekazując listę nazw
            List<String> nazwy = pasujace.stream()
                    .map(Produkt::getNazwa)   // referencja do metody (method reference)
                    .collect(Collectors.toList());
            throw new NiejednoznacznyProduktException(nazwy);
        }

        return pasujace.get(0);
    }

    /**
     * SORTOWANIE: sortuje produkty malejąco według ceny w danym miesiącu.
     *
     * Comparator.comparingDouble - tworzy komparator na podstawie lambdy.
     * reversed() - odwraca kolejność (malejąco).
     *
     * Na kolokwium: sortByDeaths, sortByVotes, itp. - ten sam wzorzec!
     */
    public static List<Produkt> sortujPoCenie(List<Produkt> lista, int rok, int miesiac) {
        return lista.stream()
                .sorted(Comparator.comparingDouble(
                        (Produkt p) -> p.getCena(rok, miesiac)
                ).reversed())
                .collect(Collectors.toList());
    }

    /**
     * toString() - nadpisuje metodę z klasy Object.
     * Wywołuje się automatycznie przy System.out.println(obiekt).
     */
    @Override
    public String toString() {
        return "Produkt{nazwa='" + nazwa + "'}";
    }
}
