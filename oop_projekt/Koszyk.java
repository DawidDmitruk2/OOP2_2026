import java.util.*;
import java.util.stream.*;

/**
 * TEMAT: KONTENERY + ENUM + KLASY WEWNĘTRZNE
 *
 * Klasa Koszyk - wzorzec z kolokwium 2023: Cart
 * Demonstruje: Map, enum, klasy wewnętrzne, obliczenia na kolekcjach
 */
public class Koszyk {

    // ===== ENUM =====
    /**
     * Typ wyliczeniowy (enum): skończony zbiór stałych wartości.
     * Automatycznie: toString(), equals(), ordinal(), values()
     *
     * Wzorzec z kolokwium 2024: enum Mode { HOUR_12, HOUR_24 }
     * Wzorzec z kolokwium 2023: enum Type { Coal, Wood, Fish }
     */
    public enum KategoriaKosztu {
        ZYWNOSC,
        PRZEMYSLOWY,
        USLUGI;

        // Enum może mieć pola i metody!
        public String getOpis() {
            return switch (this) {
                case ZYWNOSC -> "Artykuły żywnościowe";
                case PRZEMYSLOWY -> "Produkty przemysłowe";
                case USLUGI -> "Usługi";
            };
        }
    }

    // ===== MAPA: produkt → ilość =====
    // LinkedHashMap zachowuje kolejność wstawiania
    private final Map<Produkt, Integer> zawartosc = new LinkedHashMap<>();

    /**
     * Dodaje produkt do koszyka.
     * merge() - jeśli klucz istnieje: dodaje ilości; jeśli nie: wstawia nową
     *
     * @param produkt - obiekt Produkt (polimorficzny - może być spożywczy lub przemysłowy)
     * @param ilosc   - liczba sztuk
     */
    public void dodajProdukt(Produkt produkt, int ilosc) {
        if (ilosc <= 0) {
            throw new IllegalArgumentException("Ilość musi być dodatnia, a jest: " + ilosc);
        }
        // merge(klucz, wartość, funkcja_łącząca)
        zawartosc.merge(produkt, ilosc, Integer::sum);
    }

    /**
     * Oblicza wartość koszyka w danym miesiącu.
     * Wzorzec z kolokwium 2023: Cart::getPrice(int year, int month)
     */
    public double getCena(int rok, int miesiac) {
        double suma = 0;
        for (Map.Entry<Produkt, Integer> wpis : zawartosc.entrySet()) {
            Produkt p = wpis.getKey();
            int ilosc = wpis.getValue();
            suma += p.getCena(rok, miesiac) * ilosc; // POLIMORFIZM - właściwa getCena()
        }
        return suma;
    }

    /**
     * Oblicza inflację roczną między dwoma datami.
     * Wzorzec z kolokwium 2023: Cart::getInflation()
     *
     * Wzór: (cena2 - cena1) / cena1 * 100 / liczba_miesięcy * 12
     */
    public double getInflacja(int rok1, int miesiac1, int rok2, int miesiac2) {
        double cena1 = getCena(rok1, miesiac1);
        double cena2 = getCena(rok2, miesiac2);

        // Obliczamy liczbę miesięcy między datami
        int miesiace = (rok2 - rok1) * 12 + (miesiac2 - miesiac1);

        return (cena2 - cena1) / cena1 * 100.0 / miesiace * 12;
    }

    /**
     * Zwraca kopię mapy zawartości (defensywna kopia - hermetyczność).
     * Klient dostaje kopię, nie może modyfikować oryginału.
     */
    public Map<Produkt, Integer> getZawartosc() {
        return Collections.unmodifiableMap(zawartosc); // niemodyfikowalny widok
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Koszyk:\n");
        zawartosc.forEach((p, ilosc) ->
                sb.append("  ").append(p.getNazwa()).append(" x").append(ilosc).append("\n"));
        return sb.toString();
    }

    // ===== KLASA WEWNĘTRZNA (niestatyczna) =====
    /**
     * Klasa wewnętrzna ma dostęp do pól klasy zewnętrznej (Koszyk.this).
     * Użyj gdy klasa pomocnicza jest ściśle powiązana z klasą zewnętrzną.
     *
     * Niestatyczna: wymaga instancji Koszyk do stworzenia.
     * (w przeciwieństwie do 'private static class' z WzorzecCSV.java)
     */
    public class PodsumowanieKoszyka {

        /**
         * Generuje raport koszyka - może czytać pola klasy zewnętrznej.
         */
        public String generujRaport(int rok, int miesiac) {
            // Dostęp do zawartosc (pole klasy zewnętrznej Koszyk)
            int lacznaIlosc = zawartosc.values().stream().mapToInt(Integer::intValue).sum();
            double lacznaWartosc = getCena(rok, miesiac); // metoda klasy zewnętrznej

            return String.format(
                "Raport za %d/%d: %d produktów, wartość: %.2f zł",
                miesiac, rok, lacznaIlosc, lacznaWartosc
            );
        }
    }
}
