package pl.umcs.oop.server;

import pl.umcs.oop.game.Gesture;
import pl.umcs.oop.game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Obsługuje komunikację z pojedynczym klientem podłączonym do serwera.
 * Dziedziczy po Player (jest uczestnikiem gry) i implementuje Runnable
 * (działa w osobnym wątku).
 */
public class ClientHandler extends Player implements Runnable {

    private final Socket socket;         // Gniazdo TCP połączenia z klientem
    private final BufferedReader reader; // Strumień odczytu danych przychodzących od klienta
    private final PrintWriter writer;    // Strumień wysyłania danych do klienta
    private final Server server;         // Referencja do serwera (np. do wyzywania innych graczy)
    private String login;                // Login zalogowanego użytkownika

    /**
     * Inicjalizuje handler: otwiera strumienie wejściowy i wyjściowy dla danego gniazda.
     */
    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getLogin() {
        return login;
    }

    /**
     * Wysyła wiadomość tekstową do klienta przez strumień wyjściowy.
     */
    public void send(String message) {
        writer.println(message);
    }

    /**
     * Zamyka gniazdo i usuwa handler z listy aktywnych połączeń na serwerze.
     */
    private void close() throws IOException {
        socket.close();
        server.removeHandler(this);
    }

    /**
     * Główna logika obsługi klienta, wykonywana w osobnym wątku.
     *
     * Przebieg:
     * 1. Prosi klienta o login i hasło.
     * 2. Weryfikuje dane przez bazę danych; przy błędzie rozłącza.
     * 3. Po zalogowaniu czeka w pętli na wiadomości od klienta:
     *    - Jeśli gracz jest w trakcie pojedynku: interpretuje "r"/"p"/"s" jako gest.
     *    - Jeśli gracz nie pojedynkuje się: traktuje wiadomość jako login przeciwnika
     *      i próbuje go wyzwać do pojedynku.
     * 4. Po zerwaniu połączenia (readLine zwraca null) zamyka handler.
     */
    @Override
    public void run() {
        try {
            send("Podaj login:");
            String PodanyLogin = reader.readLine();

            send("Podaj haslo:");
            String PodaneHaslo = reader.readLine();

            if (!server.getDatabase().authenticate(PodanyLogin, PodaneHaslo)) {
                send("Nieprawidlowy login lub haslo");
                close();
                return;
            }
            this.login = PodanyLogin;
            send("Zalogowano: " + login);

            String message;
            while ((message = reader.readLine()) != null) {
                if (isDuelling()) {
                    // Gracz jest w pojedynku – interpretuj wiadomość jako gest
                    if (message.equals("r") || message.equals("p") || message.equals("s")) {
                        makeGesture(Gesture.fromString(message));
                    }
                } else {
                    // Gracz nie pojedynkuje się – wiadomość to login osoby do wyzwania
                    server.challengeToDuel(this, message);
                }
            }

            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
