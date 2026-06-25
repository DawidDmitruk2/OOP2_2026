package pl.umcs.oop;

import java.util.Locale;

// Przykładowy model danych przesyłanych przez sieć.
// Zamień pola na własne – zachowaj wzorzec toMessage() / fromMessage().
public record MyModel(double x, double y, String label) {

    // Serializacja: zamienia obiekt na string wysyłany przez sieć
    // Locale.ENGLISH – ważne! Zapobiega problemom z przecinkiem vs kropką
    public String toMessage() {
        return String.format(Locale.ENGLISH, "%f %f %s", x, y, label);
    }

    // Deserializacja: odtwarza obiekt ze stringa odebranego z sieci
    public static MyModel fromMessage(String message) {
        String[] parts = message.split(" ");
        double x     = Double.parseDouble(parts[0]);
        double y     = Double.parseDouble(parts[1]);
        String label = parts[2];
        return new MyModel(x, y, label);
    }
}
