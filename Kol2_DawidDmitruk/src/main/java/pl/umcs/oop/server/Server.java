package pl.umcs.oop.server;

import pl.umcs.oop.game.Duel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

/**
 * Główna klasa serwera gry Kamień-Papier-Nożyce.
 * Nasłuchuje na połączenia TCP, tworzy wątki dla każdego klienta
 * i zarządza pojedynkami między graczami.
 */
public class Server {

    private ServerSocket serverSocket;                          // Gniazdo serwera nasłuchujące na nowe połączenia
    private ArrayList<ClientHandler> handlers = new ArrayList<>(); // Lista aktualnie połączonych klientów
    private Database database = new Database();                 // Obiekt dostępu do bazy danych

    /**
     * Tworzy serwer nasłuchujący na podanym porcie TCP.
     */
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * Usuwa handler klienta z listy aktywnych połączeń
     * (wywoływane przy rozłączeniu klienta).
     */
    public void removeHandler(ClientHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Wysyła wiadomość do konkretnego klienta.
     */
    public void send(ClientHandler handler, String message) {
        handler.send(message);
    }

    /**
     * Wypisuje aktualną tabelę wyników na konsolę serwera.
     * Wywoływana po każdym zakończonym pojedynku.
     */
    private void printLeaderboard() {
        System.out.println("Leaderboard");
        for (Map.Entry<String, Integer> entry : database.getLeaderboard().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    /**
     * Obsługuje próbę wyzwania innego gracza do pojedynku.
     * Waliduje wyzwanie (czy gracz nie wyzwuje siebie, czy cel istnieje i nie jest zajęty),
     * a następnie uruchamia pojedynek.
     */
    public void challengeToDuel(ClientHandler challenger, String challengeeLogin) {
        if (challenger.getLogin().equals(challengeeLogin)) {
            challenger.send("Nie mozesz wyzwac samego siebie");
            return;
        }
        for (ClientHandler challengee : handlers) {
            if (challengee.getLogin().equals(challengeeLogin)) {
                if (challengee.isDuelling()) {
                    challenger.send(challengeeLogin + " pojedynkuje sie z kims innym");
                    return;
                }
                startDuel(challenger, challengee);
                return;
            }
        }
        challenger.send("Nie znaleziono gracza o loginie: " + challengeeLogin);
    }

    /**
     * Tworzy nowy pojedynek między dwoma klientami.
     * Ustawia callback onEnd, który po zakończeniu pojedynku:
     * - wyznacza zwycięzcę przez Duel.evaluate(),
     * - informuje obu graczy o wyniku,
     * - aktualizuje punkty w bazie danych,
     * - wypisuje aktualny leaderboard.
     */
    private void startDuel(ClientHandler challenger, ClientHandler challengee) {
        Duel duel = new Duel(challenger, challengee);
        challenger.send("Pojedynek rozpoczety z: " + challengee.getLogin());
        challengee.send("Pojedynek rozpoczety z: " + challenger.getLogin());

        duel.setOnEnd(() -> {
            Duel.Result result = duel.evaluate();
            if (result == null) {
                // Remis – obaj gracze wybrali ten sam gest
                challenger.send("Remis");
                challengee.send("Remis");
            } else {
                ClientHandler winner = (ClientHandler) result.winner();
                ClientHandler loser = (ClientHandler) result.loser();
                winner.send("You Win");
                loser.send("You Lose");
                database.updateLeaderboard(winner.getLogin(), loser.getLogin());
            }
            printLeaderboard();
        });
    }

    /**
     * Uruchamia pętlę nasłuchiwania serwera.
     * Dla każdego nowego połączenia tworzy ClientHandler i uruchamia go w osobnym wątku,
     * co pozwala obsługiwać wielu klientów jednocześnie.
     */
        public void listen() {
        System.out.println("Server started");
        while (true) {
            try {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept(); // Blokuje wątek do momentu nowego połączenia
                ClientHandler handler = new ClientHandler(socket, this);
                Thread thread = new Thread(handler);
                thread.start();      // Klient obsługiwany w osobnym wątku
                handlers.add(handler);
            } catch (IOException e) {
                System.err.println("Error polaczenia: " + e.getMessage());
            }
        }
    }

    /**
     * Punkt wejścia aplikacji – uruchamia serwer na porcie 5000.
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
        server.listen();
    }
}
