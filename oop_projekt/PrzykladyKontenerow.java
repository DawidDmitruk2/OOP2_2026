import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * TEMAT: KONTENERY + PROGRAMOWANIE GENERYCZNE + FUNKCYJNE
 *
 * Ten plik zawiera przykłady wszystkich typów kontenerów i operacji na nich.
 */

// ===== RECORD =====
// Nowa składnia Java 16+. Pojawia się od kolokwium 2025.
// record automatycznie tworzy: konstruktor, gettery, equals, hashCode, toString
// Używaj zamiast klasy gdy przechowujesz tylko dane (brak logiki).

/**
 * Record Kandydat - zawiera tylko imię i nazwisko.
 * Automatycznie dostępne: kandydat.nazwa(), kandydat.equals(), toString()
 *
 * Analogia z kolokwium 2025: record Candidate { String name; }
 */
record Kandydat(String nazwa) {
    // Można dodać metody, ale nie pola
    public String nazwisko() {
        return nazwa.split(" ")[1]; // drugie słowo to nazwisko
    }
}

/**
 * PROGRAMOWANIE GENERYCZNE: Klasa Para<A, B>
 *
 * Typ generyczny <A, B> pozwala użyć tej samej klasy z różnymi typami:
 *   Para<String, Integer> - klucz-wartość
 *   Para<Kandydat, Integer> - kandydat-głosy
 *
 * Na kolokwium: rzadziej wymagane, ale warto znać podstawy.
 */
class Para<A, B> {

    // Pola generyczne - typ A i B zostaną podane przy użyciu klasy
    private final A pierwszy;
    private final B drugi;

    public Para(A pierwszy, B drugi) {
        this.pierwszy = pierwszy;
        this.drugi = drugi;
    }

    public A getPierwszy() { return pierwszy; }
    public B getDrugi() { return drugi; }

    @Override
    public String toString() {
        return "(" + pierwszy + ", " + drugi + ")";
    }

    /**
     * Metoda generyczna - typ T określony przy wywołaniu.
     * Może być statyczna i niezależna od typów A, B klasy.
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }
}

/**
 * Klasa demonstrująca KONTENERY i PROGRAMOWANIE FUNKCYJNE.
 * Zawiera przykłady typowych operacji z kolokwiów.
 */
class PrzykladyKontenerow {

    /**
     * KONTENERY: przegląd najważniejszych typów.
     *
     * List    - lista z duplikatami, zachowuje kolejność
     * Set     - bez duplikatów, bez gwarantowanej kolejności
     * Map     - klucz→wartość, klucze unikalne
     * Queue   - kolejka FIFO
     */
    public static void demonstracjaKontenerow() {

        // ----- LIST -----
        List<String> lista = new ArrayList<>();
        lista.add("Warszawa");
        lista.add("Kraków");
        lista.add("Lublin");
        lista.add("Kraków"); // duplikat - dozwolony w List
        System.out.println("List: " + lista); // [Warszawa, Kraków, Lublin, Kraków]

        // ----- SET -----
        Set<String> zbior = new HashSet<>(lista); // konwersja z listy, usuwa duplikaty
        System.out.println("Set: " + zbior); // kolejność losowa, brak duplikatu

        // LinkedHashSet - zachowuje kolejność wstawiania
        Set<String> uporzadkowany = new LinkedHashSet<>(lista);
        System.out.println("LinkedHashSet: " + uporzadkowany);

        // ----- MAP -----
        // Typowy wzorzec z kolokwium: Map<Kandydat, Integer> głosy
        Map<String, Integer> glosy = new HashMap<>();
        glosy.put("Duda", 10000);
        glosy.put("Trzaskowski", 9500);
        glosy.put("Holownia", 3000);

        // Iteracja po mapie
        for (Map.Entry<String, Integer> wpis : glosy.entrySet()) {
            System.out.println(wpis.getKey() + ": " + wpis.getValue());
        }

        // getOrDefault - bezpieczny odczyt (nie rzuca NullPointerException)
        int wynik = glosy.getOrDefault("Nieistniejący", 0);

        // merge - dodaje do istniejącej wartości
        glosy.merge("Duda", 500, Integer::sum); // Duda: 10000 + 500 = 10500
    }

    /**
     * STREAM API: potokowe przetwarzanie danych.
     * Kluczowe operacje: filter, map, sorted, collect, reduce, groupingBy
     *
     * Wzorzec z kolokwium 2025: filtrowanie głosów po lokalizacji,
     * sumowanie głosów, znajdowanie zwycięzcy.
     */
    public static void demonstracjaStreamow(List<String> miasta) {

        // filter() - zostawia elementy spełniające warunek (lambda → boolean)
        List<String> zW = miasta.stream()
                .filter(m -> m.startsWith("W"))
                .collect(Collectors.toList());

        // map() - transformuje każdy element (String → Integer)
        List<Integer> dlugosci = miasta.stream()
                .map(String::length)           // method reference = m -> m.length()
                .collect(Collectors.toList());

        // sorted() - sortuje (naturalnie lub Comparatorem)
        List<String> posortowane = miasta.stream()
                .sorted()                      // alfabetycznie
                .collect(Collectors.toList());

        List<String> odDlugosci = miasta.stream()
                .sorted(Comparator.comparingInt(String::length).reversed()) // malejąco po długości
                .collect(Collectors.toList());

        // reduce() - agreguje do jednej wartości
        int sumaZnakow = miasta.stream()
                .mapToInt(String::length)      // IntStream - wydajniejszy dla prymitywów
                .sum();

        double srednia = miasta.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);                  // Optional - może być puste

        // collect(Collectors.groupingBy) - grupowanie (jak GROUP BY w SQL!)
        // Klucz: pierwsza litera; Wartość: lista miast z tą literą
        Map<Character, List<String>> poLiterze = miasta.stream()
                .collect(Collectors.groupingBy(m -> m.charAt(0)));

        // toMap() - konwersja do mapy
        Map<String, Integer> mapaDlugosci = miasta.stream()
                .collect(Collectors.toMap(
                        m -> m,           // klucz = miasto
                        String::length    // wartość = długość
                ));

        // anyMatch / allMatch / noneMatch
        boolean jestKrakow = miasta.stream().anyMatch(m -> m.equals("Kraków"));

        // findFirst() - pierwszy element (Optional!)
        Optional<String> pierwszeZW = miasta.stream()
                .filter(m -> m.startsWith("W"))
                .findFirst();
        pierwszeZW.ifPresent(m -> System.out.println("Znaleziono: " + m));

        System.out.println("Grupowanie: " + poLugosci);
    }

    /**
     * OBIEKTY FUNKCYJNE (interfejsy funkcyjne):
     * Function, Predicate, Consumer, Supplier - używane z lambdami.
     *
     * Na kolokwium 2023: Function<Path, Product> jako parametr addProducts()
     */
    public static void demonstracjaLambd() {

        // Function<T, R> - przyjmuje T, zwraca R
        Function<String, Integer> dlugoscSlowa = String::length;
        Function<String, String> naWielkie = String::toUpperCase;

        // Kompozycja funkcji: andThen() - najpierw naWielkie, potem dlugoscSlowa
        Function<String, Integer> dlugoscWielkich = naWielkie.andThen(dlugoscSlowa);
        System.out.println(dlugoscWielkich.apply("hello")); // 5

        // Predicate<T> - przyjmuje T, zwraca boolean
        Predicate<String> dluzejNiz5 = s -> s.length() > 5;
        Predicate<String> zaczynaOdW = s -> s.startsWith("W");

        // Łączenie predykatów
        Predicate<String> oba = dluzejNiz5.and(zaczynaOdW);
        Predicate<String> ktorys = dluzejNiz5.or(zaczynaOdW);
        Predicate<String> negacja = dluzejNiz5.negate();

        // Consumer<T> - przyjmuje T, nic nie zwraca (void)
        Consumer<String> wypisz = System.out::println;
        Consumer<String> wypiszWielkie = s -> System.out.println(s.toUpperCase());

        // Supplier<T> - nic nie przyjmuje, zwraca T
        Supplier<List<String>> nowaLista = ArrayList::new;
        List<String> lista = nowaLista.get();

        // Comparator - do sortowania
        Comparator<String> poDlugosci = Comparator.comparingInt(String::length);
        Comparator<String> poLiterzePotemDlugosci = Comparator
                .comparingInt((String s) -> s.charAt(0))
                .thenComparingInt(String::length);
    }
}
