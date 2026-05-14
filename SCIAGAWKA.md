# ŚCIĄGAWKA OOP - Wzorce z Kolokwiów Java

## 1. KLASA ABSTRAKCYJNA

```java
public abstract class Baza {
    private final String nazwa;          // prywatne + final = ustawiane raz

    public Baza(String nazwa) {          // konstruktor
        this.nazwa = nazwa;
    }

    public String getNazwa() { return nazwa; }  // akcesor

    public abstract double getCena();    // metoda abstrakcyjna - bez ciała!

    public static Baza fromCsv(Path p) { // statyczna metoda fabryczna
        // ...wczytaj plik, zwróć konkretny obiekt...
    }
}
```

## 2. DZIEDZICZENIE

```java
public class Podklasa extends Baza {
    private int pole;

    public Podklasa(String nazwa, int pole) {
        super(nazwa);      // PIERWSZE wywołanie! Konstruktor klasy nadrzędnej
        this.pole = pole;
    }

    @Override             // OBOWIĄZKOWE gdy nadpisujesz metodę abstrakcyjną
    public double getCena() {
        return pole * 2.5;
    }
}
```

## 3. WYJĄTKI CHECKED (kompilator wymusza try-catch)

```java
// Definicja
class MojWyjatek extends Exception {
    public MojWyjatek(String komunikat) {
        super(komunikat);  // getMessage() zwróci komunikat
    }
}

// Użycie - metoda musi zadeklarować throws
void metoda() throws MojWyjatek {
    throw new MojWyjatek("opis błędu");
}

// Przechwycenie
try {
    metoda();
} catch (MojWyjatek e) {
    System.out.println(e.getMessage());
}
```

## 4. WYJĄTKI UNCHECKED (brak try-catch w sygnaturze)

```java
class MojBlad extends RuntimeException {
    public MojBlad(String msg) { super(msg); }
}
// Nie wymaga 'throws' w sygnaturze metody
```

## 5. CZYTANIE PLIKU CSV

```java
// Całość naraz (małe pliki)
List<String> linie = Files.readAllLines(Path.of("plik.csv"));

// Linia po linii (try-with-resources!)
try (BufferedReader br = Files.newBufferedReader(Path.of("plik.csv"))) {
    String linia;
    while ((linia = br.readLine()) != null) {
        String[] col = linia.split(";");
    }
}

// Sprawdzenie pliku przed użyciem
if (!plik.exists() || !plik.canRead())
    throw new FileNotFoundException(sciezka.toString());
```

## 6. RECORD (Java 16+) - zastępuje prostą klasę danych

```java
record Kandydat(String nazwa, int wiek) {}
// Automatycznie: konstruktor, kandydat.nazwa(), kandydat.wiek(), equals, toString
```

## 7. KONTENERY - najważniejsze operacje

```java
// List
List<String> lista = new ArrayList<>();
lista.add("a");
lista.get(0);
lista.size();

// Map
Map<String, Integer> mapa = new HashMap<>();
mapa.put("klucz", 42);
mapa.get("klucz");                          // → 42
mapa.getOrDefault("brak", 0);              // bezpieczny odczyt
mapa.merge("klucz", 1, Integer::sum);      // dodaj do istniejącej wartości
mapa.entrySet()                             // iteracja: entry.getKey(), entry.getValue()

// Set
Set<String> zbior = new HashSet<>();
zbior.add("a");
zbior.contains("a");                        // → true (O(1))
```

## 8. STREAM API - wzorzec na kolokwium

```java
List<Produkt> wynik = lista.stream()
    .filter(p -> p.getNazwa().startsWith("A"))      // filtruj
    .map(p -> p.getCena())                          // transformuj
    .sorted(Comparator.comparingDouble(x -> x))     // sortuj
    .collect(Collectors.toList());                  // zbierz

// Suma, max, min
int suma = lista.stream().mapToInt(String::length).sum();
Optional<String> max = lista.stream().max(Comparator.naturalOrder());

// Grupowanie (jak GROUP BY)
Map<String, List<Produkt>> grupy = lista.stream()
    .collect(Collectors.groupingBy(Produkt::getNazwa));
```

## 9. SORTOWANIE - wzorzec sortByDeaths/sortByVotes

```java
// Malejąco po liczbie głosów
lista.sort(Comparator.comparingInt(Kandydat::getGlosy).reversed());

// Lub przez stream:
List<Kandydat> posortowani = lista.stream()
    .sorted(Comparator.comparingInt(Kandydat::getGlosy).reversed())
    .collect(Collectors.toList());
```

## 10. ENUM

```java
public enum Tryb { DWUDZIESTOCZTEROGODZINNY, DWUNASTOGODZINNY }

// Użycie:
Tryb t = Tryb.DWUDZIESTOCZTEROGODZINNY;
switch (t) {
    case DWUDZIESTOCZTEROGODZINNY -> "24h";
    case DWUNASTOGODZINNY -> "12h";
}
```

## 11. OBIEKTY FUNKCYJNE (interfejsy funkcyjne)

```java
Function<Path, Produkt> fromCsv = ProduktSpozywczy::fromCsv; // method reference
Produkt p = fromCsv.apply(Path.of("plik.csv"));

Predicate<String> dluzszy5 = s -> s.length() > 5;
Consumer<String> wypisz = System.out::println;
Supplier<List<String>> nowaLista = ArrayList::new;
```

## 12. KLASA WEWNĘTRZNA STATYCZNA (private static class)

```java
// Wewnątrz klasy zewnętrznej:
private static class Kolumny {
    public final int pierwsza, liczba;
    public Kolumny(int pierwsza, int liczba) {
        this.pierwsza = pierwsza;
        this.liczba = liczba;
    }
}
// Użycie: new Kolumny(0, 3) - z poziomu klasy zewnętrznej
```

## 13. POLIMORFIZM - lista różnych podtypów

```java
List<Baza> lista = new ArrayList<>();
lista.add(new Podklasa1("a"));  // oba typy do jednej listy
lista.add(new Podklasa2("b"));

for (Baza b : lista) {
    b.getCena(); // wywołuje właściwą wersję - Podklasa1 lub Podklasa2
}

// Sprawdzanie typu
if (b instanceof Podklasa1 p1) {
    p1.metodaSpecyficzna(); // dostępna bez rzutowania
}
```

## 14. DATY (LocalDate / LocalTime)

```java
LocalDate data = LocalDate.parse("1/21/21", DateTimeFormatter.ofPattern("M/d/yy"));
LocalDate dzis = LocalDate.now();
LocalTime teraz = LocalTime.now();

// Formatowanie
String tekst = data.format(DateTimeFormatter.ofPattern("d.MM.yy"));

// Operacje
LocalDate jutro = dzis.plusDays(1);
long dni = ChronoUnit.DAYS.between(data1, data2);
```

---
## CHECKLIST przed wysłaniem kolokwium:
- [ ] Klasy publiczne = osobne pliki .java (lub innerclass)
- [ ] Metody abstrakcyjne = nadpisane (@Override) we wszystkich podklasach
- [ ] Checked exceptions = deklarowane (throws) lub przechwytywane (try-catch)
- [ ] try-with-resources przy otwieraniu plików
- [ ] super() jako pierwsza linia konstruktora podklasy
- [ ] Akcesor (get...) do każdego prywatnego pola wymaganego przez zadanie
