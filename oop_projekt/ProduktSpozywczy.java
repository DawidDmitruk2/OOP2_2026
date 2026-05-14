import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TEMAT: DZIEDZICZENIE
 *
 * ProduktSpoż dziedziczy po Produkt (extends).
 * Dziedziczy wszystkie pola i metody publiczne/chronione.
 * MUSI nadpisać metody abstrakcyjne z klasy nadrzędnej.
 *
 * Analogia z kolokwium 2023: FoodProduct extends Product
 */
public class ProduktSpozywczy extends Produkt {

    // Pola specyficzne dla tej podklasy - ceny per województwo
    // Double[] (obiektowa) zamiast double[] - może być null dla brakujących danych
    private final String[] wojewodztwa;
    private final double[][] ceny; // [indeks_wojewodztwa][indeks_miesiaca]

    /**
     * KONSTRUKTOR PODKLASY:
     * Pierwsze wywołanie to super(...) - konstruktor klasy nadrzędnej.
     * Java wymaga, żeby super() było pierwsze w konstruktorze!
     */
    public ProduktSpozywczy(String nazwa, String[] wojewodztwa, double[][] ceny) {
        super(nazwa); // wywołanie konstruktora Produkt(String nazwa)
        this.wojewodztwa = wojewodztwa;
        this.ceny = ceny;
    }

    /**
     * STATYCZNA METODA FABRYCZNA (factory method):
     * Wzorzec z każdego kolokwium - fromCsv(Path path).
     *
     * Zamiast new ProduktSpozywczy(...) piszemy ProduktSpozywczy.fromCsv(sciezka).
     * Zaleta: metoda może mieć nazwę, obsłużyć wyjątki, walidować dane.
     *
     * PLIKI: czytamy plik linia po linii przez Files.readAllLines()
     */
    public static ProduktSpozywczy fromCsv(Path sciezka) {
        try {
            List<String> linie = Files.readAllLines(sciezka);

            // Linia 0: nazwa produktu
            String nazwa = linie.get(0).trim();

            // Linia 1: nagłówek - pomijamy

            // Linie 2+: województwo;cena1;cena2;...
            int liczbaMiesiecy = linie.get(2).split(";").length - 1;
            String[] wojewodztwa = new String[linie.size() - 2];
            double[][] ceny = new double[wojewodztwa.length][liczbaMiesiecy];

            for (int i = 2; i < linie.size(); i++) {
                String[] czesci = linie.get(i).split(";");
                wojewodztwa[i - 2] = czesci[0]; // pierwsza kolumna = województwo

                for (int j = 0; j < liczbaMiesiecy; j++) {
                    ceny[i - 2][j] = Double.parseDouble(czesci[j + 1]);
                }
            }

            return new ProduktSpozywczy(nazwa, wojewodztwa, ceny);

        } catch (IOException e) {
            // Owijamy checked exception w unchecked - częsty wzorzec
            throw new RuntimeException("Błąd odczytu pliku: " + sciezka, e);
        }
    }

    /**
     * NADPISANIE (Override) metody abstrakcyjnej z klasy nadrzędnej.
     * Wersja 3-argumentowa: cena w konkretnym województwie.
     *
     * @param rok     - rok (2010-2022)
     * @param miesiac - miesiąc (1-12)
     * @param woj     - nazwa województwa wielkimi literami
     */
    public double getCena(int rok, int miesiac, String woj) {
        // Walidacja zakresu dat - rzucamy IndexOutOfBoundsException
        int indeksMiesiaca = przeliczIndeks(rok, miesiac); // może rzucić wyjątek

        // Szukamy województwa w tablicy
        for (int i = 0; i < wojewodztwa.length; i++) {
            if (wojewodztwa[i].equals(woj)) {
                return ceny[i][indeksMiesiaca];
            }
        }
        // Województwo nie znalezione
        throw new IndexOutOfBoundsException("Brak województwa: " + woj);
    }

    /**
     * OVERRIDE metody abstrakcyjnej - wersja 2-argumentowa.
     * Zwraca ŚREDNIĄ ze wszystkich województw.
     * Nadpisuje metodę abstrakcyjną z Produkt - @Override to wymaga.
     */
    @Override
    public double getCena(int rok, int miesiac) {
        int indeksMiesiaca = przeliczIndeks(rok, miesiac);
        double suma = 0;
        for (double[] cenaProwincji : ceny) {
            suma += cenaProwincji[indeksMiesiaca];
        }
        return suma / ceny.length; // średnia arytmetyczna
    }

    /**
     * OVERRIDE metody abstrakcyjnej getOpis().
     */
    @Override
    public String getOpis() {
        return "ProduktSpożywczy: " + getNazwa() + " (dostępny w " + wojewodztwa.length + " województwach)";
    }

    /**
     * PRYWATNA metoda pomocnicza - przelicza rok/miesiąc na indeks tablicy.
     * Dane zaczynają się od 01.2010 (rok=2010, miesiac=1 → indeks=0).
     *
     * Rzuca wyjątek przy błędnych danych - wzorzec z kolokwium 2023.
     */
    private int przeliczIndeks(int rok, int miesiac) {
        if (miesiac < 1 || miesiac > 12) {
            throw new IndexOutOfBoundsException("Miesiąc poza zakresem 1-12: " + miesiac);
        }
        int indeks = (rok - 2010) * 12 + (miesiac - 1);
        if (indeks < 0 || indeks >= ceny[0].length) {
            throw new IndexOutOfBoundsException("Data poza zakresem: " + rok + "/" + miesiac);
        }
        return indeks;
    }
}
