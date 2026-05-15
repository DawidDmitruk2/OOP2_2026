import java.io.IOException;
import java.nio.file.*;

/**
 * WZORZEC SVG NA KOLOKWIUM
 *
 * SVG = plik tekstowy XML opisujący grafikę wektorową.
 * W Javie generujemy go przez składanie Stringów i zapis do pliku.
 *
 * Pojawia się w:
 *   - Kolokwium 2024: zegar analogowy (tarcza + wskazówki)
 *   - Kolokwium 2025: mapa Polski z województwami
 */
public class WzorzecSVG {

    // Stałe definiujące rozmiar - łatwiej zmieniać niż magiczne liczby
    private static final int SZEROKOSC = 200;
    private static final int WYSOKOSC = 200;
    private static final int SRODEK_X = 100;
    private static final int SRODEK_Y = 100;
    private static final int PROMIEN_TARCZY = 90;

    // ==================================================================
    //  BUDOWANIE PLIKU SVG - ogólny wzorzec
    // ==================================================================

    /**
     * Każdy plik SVG musi zaczynać się od tego nagłówka.
     * viewBox pozwala skalować bez zmiany współrzędnych.
     */
    private static String naglowekSVG(int szerokosc, int wysokosc) {
        return String.format(
            "<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' viewBox='0 0 %d %d'>\n",
            szerokosc, wysokosc, szerokosc, wysokosc
        );
    }

    /** Zamknięcie każdego pliku SVG */
    private static String stopkaSVG() {
        return "</svg>";
    }

    /**
     * Zapis gotowego SVG do pliku - Files.writeString() to najprościej.
     * Wzorzec z kolokwium 2024: AnalogClock.toSvg(Path sciezka)
     */
    public static void zapiszSVG(Path sciezka, String zawartoscSVG) throws IOException {
        // Tworzy katalog nadrzędny jeśli nie istnieje
        Files.createDirectories(sciezka.getParent());
        Files.writeString(sciezka, zawartoscSVG);
    }

    // ==================================================================
    //  ZNACZNIKI SVG - każdy to metoda zwracająca String
    // ==================================================================

    /**
     * OKRĄG - tarcza zegara, punkt na mapie
     *
     * cx, cy = środek
     * r      = promień
     * fill   = kolor wypełnienia ("white", "#ff0000", "none")
     * stroke = kolor obramowania
     * stroke-width = grubość obramowania
     */
    private static String okrag(int cx, int cy, int r, String fill, String stroke, int strokeWidth) {
        return String.format(
            "  <circle cx='%d' cy='%d' r='%d' fill='%s' stroke='%s' stroke-width='%d'/>\n",
            cx, cy, r, fill, stroke, strokeWidth
        );
    }

    /**
     * ODCINEK (linia) - wskazówka zegara
     *
     * x1,y1 = punkt startowy
     * x2,y2 = punkt końcowy
     *
     * KLUCZOWE: transform='rotate(kąt, cx, cy)' - obrót wokół środka tarczy
     *   kąt 0   = wskazówka w górę (12:00)
     *   kąt 90  = wskazówka w prawo (3:00)
     *   kąt 180 = wskazówka w dół (6:00)
     *   kąt 270 = wskazówka w lewo (9:00)
     */
    private static String odcinek(int x1, int y1, int x2, int y2,
                                   String kolor, int grubosc, double obrot) {
        return String.format(
            "  <line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='%d'" +
            " transform='rotate(%.2f, %d, %d)'/>\n",
            x1, y1, x2, y2, kolor, grubosc, obrot, SRODEK_X, SRODEK_Y
        );
    }

    /**
     * PROSTOKĄT - miasto na mapie (kolokwium 2023: rect w map.svg)
     *
     * x, y = lewy górny wierzchołek (NIE środek!)
     * Środek = (x + width/2, y + height/2)
     */
    private static String prostokat(double x, double y, double szerokosc, double wysokosc,
                                     String fill, String stroke) {
        return String.format(
            "  <rect x='%.2f' y='%.2f' width='%.2f' height='%.2f' fill='%s' stroke='%s'/>\n",
            x, y, szerokosc, wysokosc, fill, stroke
        );
    }

    /**
     * WIELOKĄT - kształt województwa lub lądu
     *
     * points = "x1,y1 x2,y2 x3,y3 ..." oddzielone spacjami
     * Automatycznie zamknięty (ostatni punkt łączy się z pierwszym).
     */
    private static String wielokat(String punkty, String fill, String stroke) {
        return String.format(
            "  <polygon points='%s' fill='%s' stroke='%s' stroke-width='1'/>\n",
            punkty, fill, stroke
        );
    }

    /**
     * TEKST - etykieta na mapie, cyfry na tarczy
     *
     * text-anchor='middle' = wyśrodkowany względem x,y
     */
    private static String tekst(int x, int y, String tresc, int rozmiar) {
        return String.format(
            "  <text x='%d' y='%d' font-size='%d' text-anchor='middle'" +
            " dominant-baseline='middle'>%s</text>\n",
            x, y, rozmiar, tresc
        );
    }

    // ==================================================================
    //  OBLICZANIE KĄTÓW WSKAZÓWEK - serce kolokwium 2024
    // ==================================================================

    /**
     * Wskazówka SEKUNDOWA - obrót SKOKOWY (dyskretny).
     * Kolokwium 2024, krok 9: setTime ustawia kąt na podstawie sekund.
     *
     * 60 sekund = 360 stopni → 1 sekunda = 6 stopni
     */
    public static double katSekundnika(int sekundy) {
        return sekundy * 6.0; // np. sekunda 30 → kąt 180°
    }

    /**
     * Wskazówka MINUTOWA - obrót CIĄGŁY (z dokładnością do sekundy).
     * Kolokwium 2024, krok 10: "ruchem ciągłym z dokładnością do jednej sekundy"
     *
     * 60 minut = 360 stopni → 1 minuta = 6 stopni
     * Każda sekunda przesuwa o 6/60 = 0.1 stopnia
     */
    public static double katMinutnika(int minuty, int sekundy) {
        return minuty * 6.0 + sekundy * 0.1;
        // np. 15 min 30 sek → 15*6 + 30*0.1 = 90 + 3 = 93°
    }

    /**
     * Wskazówka GODZINOWA - obrót CIĄGŁY.
     *
     * 12 godzin = 360 stopni → 1 godzina = 30 stopni
     * Każda minuta przesuwa o 30/60 = 0.5 stopnia
     * Każda sekunda przesuwa o 0.5/60 ≈ 0.00833 stopnia
     */
    public static double katGodzinnika(int godziny, int minuty, int sekundy) {
        int g12 = godziny % 12; // zegar analogowy ma 12 godzin, nie 24
        return g12 * 30.0 + minuty * 0.5 + sekundy * (0.5 / 60.0);
        // np. 13:15:30 → (13%12)*30 + 15*0.5 + 30*0.00833 = 30 + 7.5 + 0.25 = 37.75°
    }

    // ==================================================================
    //  KOMPLETNY PRZYKŁAD: ZEGAR ANALOGOWY (kolokwium 2024)
    // ==================================================================

    /**
     * Generuje kompletny SVG zegara analogowego.
     * Odpowiednik metody AnalogClock.toSvg() z kolokwium 2024.
     *
     * @param godziny  - 0-23
     * @param minuty   - 0-59
     * @param sekundy  - 0-59
     * @param sciezka  - gdzie zapisać plik
     */
    public static void generujZegar(int godziny, int minuty, int sekundy, Path sciezka)
            throws IOException {

        StringBuilder svg = new StringBuilder();

        // 1. Nagłówek
        svg.append(naglowekSVG(SZEROKOSC, WYSOKOSC));

        // 2. Tarcza - biały okrąg z czarnym obramowaniem
        svg.append(okrag(SRODEK_X, SRODEK_Y, PROMIEN_TARCZY, "white", "black", 3));

        // 3. Środkowa kropka
        svg.append(okrag(SRODEK_X, SRODEK_Y, 3, "black", "black", 1));

        // 4. Cyfry 12, 3, 6, 9
        svg.append(tekst(SRODEK_X, SRODEK_Y - 75, "12", 14));
        svg.append(tekst(SRODEK_X + 75, SRODEK_Y, "3", 14));
        svg.append(tekst(SRODEK_X, SRODEK_Y + 75, "6", 14));
        svg.append(tekst(SRODEK_X - 75, SRODEK_Y, "9", 14));

        // 5. Wskazówka godzinowa (gruba, krótka, czarna)
        // Rysujemy pionowo w górę od środka, potem obracamy
        double katG = katGodzinnika(godziny, minuty, sekundy);
        svg.append(odcinek(
            SRODEK_X, SRODEK_Y,           // start: środek tarczy
            SRODEK_X, SRODEK_Y - 55,      // koniec: 55px w górę od środka
            "black", 5, katG
        ));

        // 6. Wskazówka minutowa (średnia, długa, czarna)
        double katM = katMinutnika(minuty, sekundy);
        svg.append(odcinek(
            SRODEK_X, SRODEK_Y,
            SRODEK_X, SRODEK_Y - 75,      // 75px = prawie do krawędzi
            "black", 3, katM
        ));

        // 7. Wskazówka sekundowa (cienka, czerwona, najdłuższa)
        double katS = katSekundnika(sekundy);
        svg.append(odcinek(
            SRODEK_X, SRODEK_Y,
            SRODEK_X, SRODEK_Y - 82,
            "red", 1, katS
        ));

        // 8. Stopka
        svg.append(stopkaSVG());

        // 9. Zapis
        zapiszSVG(sciezka, svg.toString());
        System.out.println("Zapisano: " + sciezka);
    }

    // ==================================================================
    //  KOMPLETNY PRZYKŁAD: MAPA Z WIELOKĄTAMI (kolokwium 2025)
    // ==================================================================

    /**
     * Generuje SVG mapy z kolorowymi regionami.
     * Odpowiednik VoivodeshipMap.saveToSvg() z kolokwium 2025.
     *
     * @param kolor - metoda wywoływana dla każdego regionu (łatwa do nadpisania w podklasie!)
     */
    public static void generujMape(Path sciezka) throws IOException {
        StringBuilder svg = new StringBuilder();
        svg.append(naglowekSVG(400, 300));

        // Przykładowe regiony jako wielokąty
        // W prawdziwym zadaniu: ścieżki są dane w klasie VoivodeshipMap
        svg.append(wielokat("50,50 150,50 150,150 50,150", "lightblue", "black")); // Region 1
        svg.append(wielokat("150,50 300,30 280,160 150,150", "lightgreen", "black")); // Region 2
        svg.append(wielokat("50,150 150,150 130,250 40,240", "lightyellow", "black")); // Region 3

        // Etykiety regionów
        svg.append(tekst(100, 100, "Lubelskie", 12));
        svg.append(tekst(220, 95, "Mazowieckie", 12));

        svg.append(stopkaSVG());
        zapiszSVG(sciezka, svg.toString());
    }

    // ==================================================================
    //  WZORZEC DZIEDZICZENIA DLA SVG (kolokwium 2024, kroki 8-11)
    // ==================================================================

    /*
     * Klasa abstrakcyjna ClockHand - wskazówka zegara
     * (poniżej jako kod do przeczytania, nie do kompilacji - uproszczone)
     *
     *   abstract class ClockHand {
     *       protected double kat; // kąt obrotu wskazówki
     *
     *       public abstract void setTime(LocalTime czas);
     *       public abstract String toSvg();
     *   }
     *
     *   class SecondHand extends ClockHand {
     *       @Override
     *       public void setTime(LocalTime czas) {
     *           kat = czas.getSecond() * 6.0; // SKOKOWY - tylko sekundy
     *       }
     *       @Override
     *       public String toSvg() {
     *           return String.format(
     *               "<line x1='100' y1='100' x2='100' y2='18' stroke='red' stroke-width='1'" +
     *               " transform='rotate(%.2f, 100, 100)'/>", kat);
     *       }
     *   }
     *
     *   class MinuteHand extends ClockHand {
     *       @Override
     *       public void setTime(LocalTime czas) {
     *           // CIĄGŁY - minuty + ułamek z sekund
     *           kat = czas.getMinute() * 6.0 + czas.getSecond() * 0.1;
     *       }
     *       @Override
     *       public String toSvg() {
     *           return String.format(
     *               "<line x1='100' y1='100' x2='100' y2='25' stroke='black' stroke-width='3'" +
     *               " transform='rotate(%.2f, 100, 100)'/>", kat);
     *       }
     *   }
     *
     *   class AnalogClock extends Clock {
     *       // Lista polimorficzna - krok 11
     *       private final List<ClockHand> wskazowki = List.of(
     *           new SecondHand(), new MinuteHand(), new HourHand()
     *       );
     *
     *       public void toSvg(Path sciezka) throws IOException {
     *           StringBuilder svg = new StringBuilder();
     *           svg.append("<svg ...>\n");
     *           svg.append("  <circle .../>\n"); // tarcza
     *
     *           for (ClockHand w : wskazowki) {
     *               svg.append("  ").append(w.toSvg()).append("\n");
     *           }
     *
     *           svg.append("</svg>");
     *           Files.writeString(sciezka, svg.toString());
     *       }
     *
     *       // Kluczowy trick: żeby setTime() automatycznie ustawiał wskazówki
     *       // BEZ nadpisywania (override) - krok 11
     *       // → użyj wzorca Observer lub wywołaj update z Clock.setTime():
     *       //
     *       //   W Clock.setTime() po ustawieniu czasu:
     *       //     onTimeChanged(LocalTime.of(h, m, s));
     *       //
     *       //   protected void onTimeChanged(LocalTime t) {} // pusta w Clock
     *       //
     *       //   @Override
     *       //   protected void onTimeChanged(LocalTime t) { // w AnalogClock
     *       //       wskazowki.forEach(w -> w.setTime(t));
     *       //   }
     */

    public static void main(String[] args) throws IOException {
        // Test: zegar na 13:15:30
        generujZegar(13, 15, 30, Path.of("zegar_wyjscie/zegar.svg"));

        // Test: mapa
        generujMape(Path.of("mapa_wyjscie/mapa.svg"));

        // Test kątów
        System.out.println("Kąt sekundnika (30s): " + katSekundnika(30) + "°"); // 180°
        System.out.println("Kąt minutnika (15m30s): " + katMinutnika(15, 30) + "°"); // 93°
        System.out.println("Kąt godzinnika (13:15:30): " + katGodzinnika(13, 15, 30) + "°"); // 37.75°
    }
}
