package pl.umcs.oop.circleapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Serwer TCP aplikacji CircleApp.
 * Nasłuchuje na połączenia od klientów JavaFX, tworzy wątek dla każdego z nich
 * i rozgłasza (broadcast) wiadomości do wszystkich podłączonych klientów.
 */
public class Server {
    private final ServerSocket serverSocket;
    // CopyOnWriteArrayList zamiast ArrayList: wątek-bezpieczna lista.
    // broadcast() iteruje po liście, a jednocześnie inne wątki (ClientThread)
    // mogą wywoływać removeHandler() – ArrayList rzuciłaby ConcurrentModificationException.
    private CopyOnWriteArrayList<ClientThread> handlers = new CopyOnWriteArrayList<>();


    /**
     * Tworzy gniazdo serwera nasłuchujące na podanym porcie.
     */
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Usuwa ClientThread z listy aktywnych klientów po jego rozłączeniu.
     * Wywoływane przez ClientThread.close().
     */
    public void removeHandler(ClientThread ct) {
        handlers.remove(ct);
    }

    /**
     * Wysyła wiadomość "disconnected" do wszystkich klientów i czyści listę.
     * Wywoływane przez shutdown hook przy zamykaniu serwera (Ctrl+C).
     */
    public void disconnectHandlers() {
        handlers.forEach(handler -> handler.send("disconnected"));
        handlers.clear();
    }

    /**
     * Rozgłasza wiadomość do wszystkich aktualnie podłączonych klientów.
     * Wywoływane przez ClientThread.run() po odebraniu wiadomości od jednego klienta.
     */
    public void broadcast(String message) {
        for (ClientThread ct : handlers) {
            ct.send(message);
        }
    }

    /**
     * Główna pętla serwera – nasłuchuje na nowe połączenia TCP.
     * Dla każdego nowego klienta tworzy ClientThread i uruchamia go w osobnym wątku,
     * dzięki czemu serwer może obsługiwać wielu klientów równocześnie.
     */
    public void listen() throws IOException {
        System.out.println("Server started");
        while (true) {
            System.out.println("Waiting for connection...");
            Socket socket = serverSocket.accept(); // blokuje do momentu nowego połączenia
            ClientThread ct = new ClientThread(socket, this);
            Thread thread = new Thread(ct);
            thread.start(); // klient obsługiwany w osobnym wątku
            handlers.add(ct);
        }
    }

    /**
     * Punkt wejścia serwera.
     * Rejestruje shutdown hook który przy zamknięciu JVM (Ctrl+C, kill)
     * grzecznie rozłącza wszystkich klientów przed wyjściem.
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);

        // Shutdown hook: wywoływany automatycznie gdy JVM jest zamykana
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.disconnectHandlers();
        }));

        server.listen();
    }
}
