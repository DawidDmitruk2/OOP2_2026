import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * TEMAT: PLIKI + WYJĄTKI - wzorce wczytywania CSV
 *
 * Wszystkie kolokwia mają zadanie z wczytywaniem pliku CSV.
 * Ten plik pokazuje kompletny wzorzec z obsługą wyjątków.
 */
public class WzorzecCSV {

    /**
     * METODA 1: Wczytywanie CSV przez Files.readAllLines()
     * Najprościej - cały plik od razu do pamięci.
     * Dobra dla małych plików.
     *
     * @param sciezka - ścieżka do pliku
     * @return lista rekordów jako List<String[]>
     * @throws IOException gdy plik nie istnieje lub nie można go odczytać
     */
    public static List<String[]> wczytajCSV(Path sciezka) throws IOException {
        List<String[]> wynik = new ArrayList<>();

        // Files.readAllLines() - zwraca listę wszystkich linii
        List<String> linie = Files.readAllLines(sciezka);

        // Pomijamy nagłówek (linia 0)
        for (int i = 1; i < linie.size(); i++) {
            String[] czesci = linie.get(i).split(";"); // split po separatorze
            wynik.add(czesci);
        }

        return wynik;
    }

    /**
     * METODA 2: Wczytywanie przez BufferedReader
     * Bardziej kontrolowane - linia po linii.
     * Dobra gdy plik jest duży lub potrzebujemy przetwarzać linia po linii.
     *
     * try-with-resources: automatycznie zamyka BufferedReader po wyjściu z bloku try
     * nawet jeśli zostanie rzucony wyjątek!
     */
    public static void wczytajPrzezBufferedReader(Path sciezka) throws IOException {

        // try-with-resources - kluczowy wzorzec dla plików!
        try (BufferedReader czytnik = Files.newBufferedReader(sciezka)) {

            String naglowek = czytnik.readLine(); // pierwsza linia = nagłówek
            System.out.println("Nagłówek: " + naglowek);

            String linia;
            // readLine() zwraca null gdy koniec pliku
            while ((linia = czytnik.readLine()) != null) {
                if (linia.isBlank()) continue; // pomiń puste linie

                String[] czesci = linia.split(";");
                // przetwarzanie...
                System.out.println("Kolumna 0: " + czesci[0]);
            }
        } // BufferedReader zamykany automatycznie tutaj
    }

    /**
     * METODA 3: Zapis do pliku
     * Wzorzec z kolokwium 2021: saveToDataFile()
     *
     * @param sciezka - ścieżka wynikowa (zakładamy poprawność)
     * @param dane    - lista wierszy do zapisu
     */
    public static void zapiszDoPliku(Path sciezka, List<String> dane) throws IOException {

        // try-with-resources dla zapisu
        try (BufferedWriter pisarz = Files.newBufferedWriter(sciezka)) {
            for (String wiersz : dane) {
                pisarz.write(wiersz);
                pisarz.newLine(); // nowa linia (OS-niezależna)
            }
        }
    }

    /**
     * METODA 4: Sprawdzanie pliku przed użyciem
     * Wzorzec z kolokwium 2021: setFiles() z walidacją.
     *
     * @throws FileNotFoundException gdy plik nie istnieje lub nie można go czytać
     */
    public static void sprawdzPlik(Path sciezka) throws FileNotFoundException {
        // toFile() konwertuje Path na File (starsze API)
        File plik = sciezka.toFile();

        if (!plik.exists() || !plik.canRead()) {
            // FileNotFoundException przyjmuje ścieżkę jako String
            throw new FileNotFoundException(sciezka.toString());
        }
    }

    /**
     * PARSOWANIE DAT: LocalDate - używane w kolokwium 2021
     *
     * LocalDate.parse() + DateTimeFormatter - przetwarzanie dat z CSV
     */
    public static LocalDate parsujDate(String tekstData) {
        // Format amerykański: M/d/yy (np. "1/21/21" = 21 stycznia 2021)
        DateTimeFormatter formatAmerykanski = DateTimeFormatter.ofPattern("M/d/yy");

        // Format europejski: d.MM.yy (np. "21.01.21")
        DateTimeFormatter formatEuropejski = DateTimeFormatter.ofPattern("d.MM.yy");

        return LocalDate.parse(tekstData, formatAmerykanski);
    }

    /**
     * PRYWATNA KLASA WEWNĘTRZNA (inner class):
     * Wzorzec z kolokwium 2021: prywatna statyczna klasa CountryColumns.
     *
     * Używana wewnętrznie do przechowywania wyników parsowania.
     * 'static' → nie potrzebuje instancji klasy zewnętrznej.
     */
    private static class KolumnyDanych {

        // Pola publiczne i finalne - ustawiane w konstruktorze
        public final int indeksPierwszejKolumny;
        public final int liczbaKolumn;

        public KolumnyDanych(int indeksPierwszejKolumny, int liczbaKolumn) {
            this.indeksPierwszejKolumny = indeksPierwszejKolumny;
            this.liczbaKolumn = liczbaKolumn;
        }
    }

    /**
     * Metoda używająca prywatnej klasy wewnętrznej.
     * Wzorzec: getCountryColumns() z kolokwium 2021
     */
    private static KolumnyDanych znajdzKolumny(String[] naglowek, String szukana)
            throws BrakProduktuException {

        int pierwsza = -1;
        int liczba = 0;

        for (int i = 0; i < naglowek.length; i++) {
            if (naglowek[i].equals(szukana)) {
                if (pierwsza == -1) pierwsza = i;
                liczba++;
            }
        }

        if (pierwsza == -1) {
            throw new BrakProduktuException(szukana);
        }

        return new KolumnyDanych(pierwsza, liczba);
    }

    /**
     * INTERFEJS FUNKCYJNY - własna definicja
     * Można użyć jako typ parametru metody, przekazać lambdę.
     *
     * Na kolokwium 2023: Function<Path, Product> jako obiekt funkcyjny do fromCsv
     * Tutaj: własny interfejs dla demonstracji
     */
    @FunctionalInterface
    interface ParsujLinie<T> {
        T parsuj(String linia) throws Exception;
    }

    /**
     * Generyczna metoda wczytująca CSV z dowolnym parserem.
     * Łączy: generyki + pliki + interfejsy funkcyjne.
     *
     * @param sciezka - plik CSV
     * @param parser  - lambda lub method reference do parsowania linii
     */
    public static <T> List<T> wczytajZParserem(Path sciezka, ParsujLinie<T> parser)
            throws IOException {
        List<T> wynik = new ArrayList<>();
        List<String> linie = Files.readAllLines(sciezka);

        for (int i = 1; i < linie.size(); i++) {
            try {
                T obiekt = parser.parsuj(linie.get(i));
                wynik.add(obiekt);
            } catch (Exception e) {
                System.err.println("Błąd parsowania linii " + i + ": " + e.getMessage());
            }
        }
        return wynik;
    }
}
