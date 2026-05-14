import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * KLASA GŁÓWNA - demonstracja wszystkich wzorców z kolokwiów OOP
 *
 * Uruchom metodą: java Main
 *
 * Każda sekcja odpowiada jednemu tematowi z listy na kolokwium.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("===== WZORCE Z KOLOKWIÓW OOP =====\n");

        // Każdy blok try-catch odpowiada osobnemu tematowi

        // ----------------------------------------------------------------
        demonstrujAbstrakcjeIDziedziczenie();
        // ----------------------------------------------------------------
        demonstrujWyjatki();
        // ----------------------------------------------------------------
        demonstrujKontenery();
        // ----------------------------------------------------------------
        demonstrujProgramowanieFunkcyjne();
        // ----------------------------------------------------------------
        demonstrujGeneryki();
        // ----------------------------------------------------------------
        demonstrujEnum();
    }

    // ==================================================================
    //  ABSTRAKCJA + DZIEDZICZENIE + POLIMORFIZM
    // ==================================================================
    static void demonstrujAbstrakcjeIDziedziczenie() {
        System.out.println("=== ABSTRAKCJA + DZIEDZICZENIE ===");

        // Nie możemy: new Produkt(...) - klasa abstrakcyjna!
        // Możemy tworzyć konkretne podklasy i przypisywać do typu nadrzędnego

        // POLIMORFIZM: zmienna typu Produkt trzyma ProduktPrzemyslowy
        Produkt p1 = new ProduktPrzemyslowy("Benzyna 95", new Double[]{5.5, 5.7, 6.0, 5.9});

        // Wywołanie metody abstrakcyjnej - Java wywoła wersję z ProduktPrzemyslowy
        System.out.println("Cena 2010/01: " + p1.getCena(2010, 1)); // → ProduktPrzemyslowy.getCena()

        // LISTA POLIMORFICZNA: przechowuje różne podtypy Produkt
        List<Produkt> produkty = new ArrayList<>();
        produkty.add(p1);
        // produkty.add(new ProduktSpozywczy(...)); // można dodać oba typy!

        for (Produkt p : produkty) {
            System.out.println(p.getOpis()); // wywołuje właściwą wersję getOpis()
        }

        // instanceof + pattern matching (Java 16+)
        if (p1 instanceof ProduktPrzemyslowy pp) {
            // 'pp' jest już rzutowany - nie trzeba osobnego rzutowania
            System.out.println("To ProduktPrzemysłowy: " + pp.getNazwa());
        }

        System.out.println();
    }

    // ==================================================================
    //  WYJĄTKI
    // ==================================================================
    static void demonstrujWyjatki() {
        System.out.println("=== WYJĄTKI ===");

        // --- CHECKED EXCEPTION: kompilator wymusza try-catch ---
        try {
            // BrakProduktuException to checked exception (extends Exception)
            throw new BrakProduktuException("Polska");
        } catch (BrakProduktuException e) {
            // getMessage() zwraca nazwę szukanego produktu/kraju
            System.out.println("Brak produktu: " + e.getMessage());
        }

        // --- MULTICATCH: przechwytywanie kilku wyjątków naraz ---
        try {
            String[] tab = new String[3];
            String s = tab[10]; // ArrayIndexOutOfBoundsException

        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            // Ten sam blok obsługuje oba typy
            System.out.println("Błąd tablicy lub null: " + e.getClass().getSimpleName());
        }

        // --- FINALLY: wykonuje się zawsze, nawet po wyjątku ---
        try {
            int wynik = 10 / 0; // ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("Złapano: " + e.getMessage());
        } finally {
            System.out.println("Finally zawsze się wykonuje");
        }

        // --- UNCHECKED: BrakZwyciezcyException (extends RuntimeException) ---
        // Nie wymaga try-catch, ale można przechwycić
        try {
            throw new BrakZwyciezcyException("Nikt nie przekroczył 50%");
        } catch (BrakZwyciezcyException e) {
            System.out.println("Unchecked: " + e.getMessage());
        }

        // --- PROPAGACJA WYJĄTKU (throws) ---
        // Metoda deklarująca 'throws BrakProduktuException' przekazuje wyjątek dalej
        // zamiast go łapać. Musi go złapać wywołujący.
        try {
            metodaRzucajacaWyjatekChecked();
        } catch (BrakProduktuException e) {
            System.out.println("Złapano dalej: " + e.getMessage());
        }

        System.out.println();
    }

    // Metoda deklaruje throws - musi to łapać wywołujący
    static void metodaRzucajacaWyjatekChecked() throws BrakProduktuException {
        throw new BrakProduktuException("CośTamCzego brak");
    }

    // ==================================================================
    //  KONTENERY
    // ==================================================================
    static void demonstrujKontenery() {
        System.out.println("=== KONTENERY ===");

        // List - ze zduplikowanymi elementami
        List<String> lista = new ArrayList<>(List.of("Lublin", "Warszawa", "Kraków", "Lublin"));
        System.out.println("List: " + lista);

        // Set - bez duplikatów
        Set<String> zbior = new LinkedHashSet<>(lista);
        System.out.println("Set: " + zbior);

        // Map - klucz:wartość
        Map<String, Integer> glosy = new HashMap<>();
        glosy.put("Kandydat A", 5000);
        glosy.put("Kandydat B", 3000);
        glosy.put("Kandydat C", 2000);

        // Suma wartości mapy
        int suma = glosy.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Łączna liczba głosów: " + suma);

        // Kandydat z max głosami
        Optional<Map.Entry<String, Integer>> zwyciezca = glosy.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        zwyciezca.ifPresent(e ->
                System.out.println("Zwycięzca: " + e.getKey() + " (" + e.getValue() + ")"));

        // Sortowanie listy różnymi sposobami
        lista.sort(String::compareTo);           // naturalnie
        lista.sort(Comparator.reverseOrder());   // odwrotnie
        lista.sort(Comparator.comparingInt(String::length)); // po długości

        System.out.println("Posortowane po długości: " + lista);
        System.out.println();
    }

    // ==================================================================
    //  PROGRAMOWANIE FUNKCYJNE - STREAM API
    // ==================================================================
    static void demonstrujProgramowanieFunkcyjne() {
        System.out.println("=== PROGRAMOWANIE FUNKCYJNE ===");

        List<Integer> liczby = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter + map + collect - potok przetwarzania
        List<Integer> parzystePodniesioneDo2 = liczby.stream()
                .filter(n -> n % 2 == 0)          // [2,4,6,8,10]
                .map(n -> n * n)                   // [4,16,36,64,100]
                .collect(Collectors.toList());
        System.out.println("Parzyste^2: " + parzystePodniesioneDo2);

        // reduce - sumowanie
        int suma = liczby.stream().reduce(0, Integer::sum);
        System.out.println("Suma: " + suma);

        // groupingBy - grupowanie (jak GROUP BY w SQL)
        Map<Boolean, List<Integer>> grupyParzystosci = liczby.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Parzyste: " + grupyParzystosci.get(true));

        // Łańcuch komparatorów - wzorzec z kolokwium sortByDeaths / sortByVotes
        List<String> miasta = List.of("Kraków", "Lublin", "Wrocław", "Gdańsk", "Łódź");
        List<String> posortowane = miasta.stream()
                .sorted(Comparator.comparingInt(String::length)
                        .thenComparing(Comparator.naturalOrder())) // remis: alfabetycznie
                .collect(Collectors.toList());
        System.out.println("Posortowane: " + posortowane);

        // Method references - skrócona składnia lambd
        // String::length = s -> s.length()
        // System.out::println = s -> System.out.println(s)
        // Integer::sum = (a, b) -> a + b
        // ArrayList::new = () -> new ArrayList<>()
        List<String> kopiaList = miasta.stream()
                .collect(Collectors.toCollection(ArrayList::new));

        System.out.println();
    }

    // ==================================================================
    //  GENERYKI
    // ==================================================================
    static void demonstrujGeneryki() {
        System.out.println("=== GENERYKI ===");

        // Klasa generyczna Para<A, B>
        Para<String, Integer> para = new Para<>("wynik", 42);
        System.out.println("Para: " + para);
        System.out.println("Pierwszy: " + para.getPierwszy());

        // Generyczna metoda statyczna
        String wiekszy = Para.max("alpha", "beta");
        System.out.println("Max string: " + wiekszy);

        int wiekszyInt = Para.max(10, 20);
        System.out.println("Max int: " + wiekszyInt);

        // Lista generyczna z wildcards
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.0, 2.0, 3.0);

        // ? extends Number - przyjmuje List<Integer>, List<Double>, List<Number>
        System.out.println("Suma intów: " + sumujListe(ints));
        System.out.println("Suma doubles: " + sumujListe(doubles));

        System.out.println();
    }

    // ? extends Number: metoda przyjmuje listę dowolnego podtypu Number
    static double sumujListe(List<? extends Number> lista) {
        return lista.stream().mapToDouble(Number::doubleValue).sum();
    }

    // ==================================================================
    //  ENUM + SWITCH EXPRESSION
    // ==================================================================
    static void demonstrujEnum() {
        System.out.println("=== ENUM ===");

        // Użycie enum jako argument / wartość
        Koszyk.KategoriaKosztu kat = Koszyk.KategoriaKosztu.ZYWNOSC;
        System.out.println("Kategoria: " + kat);
        System.out.println("Opis: " + kat.getOpis());

        // switch expression z enumem (Java 14+)
        String symbol = switch (kat) {
            case ZYWNOSC    -> "🥦";
            case PRZEMYSLOWY -> "🔧";
            case USLUGI     -> "🛎";
        };
        System.out.println("Symbol: " + symbol);

        // Iteracja po wszystkich wartościach enum
        System.out.print("Wszystkie kategorie: ");
        for (Koszyk.KategoriaKosztu k : Koszyk.KategoriaKosztu.values()) {
            System.out.print(k + " ");
        }
        System.out.println();

        // Record - używamy jako klucz mapy
        Kandydat k1 = new Kandydat("Jan Kowalski");
        Kandydat k2 = new Kandydat("Anna Nowak");
        Map<Kandydat, Integer> glosy = new HashMap<>();
        glosy.put(k1, 5000);
        glosy.put(k2, 3000);

        // Dostęp przez getter rekordu: kandydat.nazwa()
        glosy.forEach((k, v) -> System.out.println(k.nazwa() + ": " + v));
    }
}
