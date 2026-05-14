import java.util.List;

/**
 * TEMAT: WYJĄTKI - własne klasy wyjątków
 *
 * Na każdym kolokwium pojawia się co najmniej jeden własny wyjątek.
 * Kluczowe rozróżnienie:
 *
 * 1. CHECKED (sprawdzany) - extends Exception
 *    - Kompilator wymusza przechwycenie (try-catch) lub deklarację (throws)
 *    - Używaj gdy błąd jest "przewidywalny" (brak pliku, nieprawidłowe dane)
 *
 * 2. UNCHECKED (niesprawdzany) - extends RuntimeException
 *    - Kompilator NIE wymusza przechwycenia
 *    - Używaj dla błędów programistycznych
 */

// ===== WYJĄTEK CHECKED =====
// Klasa zewnętrzna - musi być w osobnym pliku lub być statyczną klasą wewnętrzną
// Tutaj używamy osobnych plików dla przejrzystości

/**
 * NiejednoznacznyProduktException - rzucany gdy prefiks pasuje do wielu produktów.
 * extends Exception → CHECKED → kompilator wymaga throws w sygnaturze metody.
 *
 * Analogia z kolokwium 2023: AmbigiousProductException
 */
class NiejednoznacznyProduktException extends Exception {

    // Przechowujemy listę pasujących nazw - dostępna przez getMessage() lub getNazwy()
    private final List<String> nazwy;

    /**
     * Konstruktor przyjmuje listę niejednoznacznych nazw.
     * getMessage() powinno zwracać czytelny komunikat - sprawdzane na kolokwium!
     */
    public NiejednoznacznyProduktException(List<String> nazwy) {
        // super(komunikat) - ustawia wiadomość zwracaną przez getMessage()
        super("Niejednoznaczny prefiks - pasujące produkty: " + nazwy);
        this.nazwy = nazwy;
    }

    /**
     * Getter do listy nazw - przydatny do wyświetlenia szczegółów.
     */
    public List<String> getNazwy() {
        return nazwy;
    }
}

/**
 * BrakProduktuException - rzucany gdy produkt nie istnieje.
 * extends Exception → CHECKED
 *
 * Analogia z kolokwium 2021: CountryNotFoundException
 * Kluczowe: getMessage() zwraca nazwę szukanego produktu/kraju.
 */
class BrakProduktuException extends Exception {

    private final String szukanaNazwa;

    /**
     * Konstruktor przyjmuje nazwę, która nie została znaleziona.
     * getMessage() zwraca tę nazwę - to jest WYMAGANE na kolokwium!
     */
    public BrakProduktuException(String szukanaNazwa) {
        super(szukanaNazwa); // getMessage() zwróci szukanaNazwę
        this.szukanaNazwa = szukanaNazwa;
    }

    public String getSzukanaNazwa() {
        return szukanaNazwa;
    }
}

/**
 * BrakZwyciezcyException - rzucany gdy nikt nie wygrał (np. brak >50% głosów).
 * extends RuntimeException → UNCHECKED → nie wymaga throws w sygnaturze.
 *
 * Analogia z kolokwium 2025: NoWinnerException
 */
class BrakZwyciezcyException extends RuntimeException {

    public BrakZwyciezcyException() {
        super("Żaden kandydat nie zdobył wymaganej liczby głosów");
    }

    public BrakZwyciezcyException(String komunikat) {
        super(komunikat);
    }
}
