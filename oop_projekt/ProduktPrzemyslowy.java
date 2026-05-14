import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TEMAT: DZIEDZICZENIE - druga podklasa
 *
 * ProduktPrzemysłowy też dziedziczy po Produkt, ale ma prostszą strukturę:
 * jedna cena na miesiąc (bez podziału na województwa).
 *
 * Analogia z kolokwium 2023: NonFoodProduct extends Product
 *
 * Widać tu POLIMORFIZM: zarówno ProduktSpozywczy jak i ProduktPrzemyslowy
 * to Produkt - można je przechowywać w List<Produkt>.
 */
public class ProduktPrzemyslowy extends Produkt {

    // Tablica cen: indeks = liczba miesięcy od 01.2010
    private final Double[] ceny;

    /**
     * Konstruktor - wywołuje super() z nazwą.
     */
    public ProduktPrzemyslowy(String nazwa, Double[] ceny) {
        super(nazwa);
        this.ceny = ceny;
    }

    /**
     * Statyczna metoda fabryczna - ten sam wzorzec co ProduktSpozywczy.fromCsv()
     * Na kolokwium często oba typy mają analogiczną metodę fromCsv.
     */
    public static ProduktPrzemyslowy fromCsv(Path sciezka) {
        try {
            List<String> linie = Files.readAllLines(sciezka);
            String nazwa = linie.get(0).trim();

            // Linia 1: nagłówek z datami - pomijamy (zakładamy poprawność)
            // Linia 2: jedna linia z cenami oddzielonymi średnikami
            String[] wartosci = linie.get(2).split(";");
            Double[] ceny = new Double[wartosci.length];
            for (int i = 0; i < wartosci.length; i++) {
                ceny[i] = Double.parseDouble(wartosci[i]);
            }

            return new ProduktPrzemyslowy(nazwa, ceny);

        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu pliku: " + sciezka, e);
        }
    }

    /**
     * OVERRIDE: implementacja metody abstrakcyjnej.
     * Przelicza rok/miesiąc na indeks i zwraca cenę.
     */
    @Override
    public double getCena(int rok, int miesiac) {
        if (miesiac < 1 || miesiac > 12) {
            throw new IndexOutOfBoundsException("Miesiąc: " + miesiac);
        }
        int indeks = (rok - 2010) * 12 + (miesiac - 1);
        if (indeks < 0 || indeks >= ceny.length) {
            throw new IndexOutOfBoundsException("Data poza zakresem: " + rok + "/" + miesiac);
        }
        return ceny[indeks];
    }

    @Override
    public String getOpis() {
        return "ProduktPrzemysłowy: " + getNazwa();
    }
}
