package pl.umcs.oop.server;

import pl.umcs.oop.server.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final ServerSocket serverSocket;

    // CopyOnWriteArrayList – bezpieczna wielowątkowo (broadcast iteruje, inne wątki usuwają)
    private final CopyOnWriteArrayList<ClientThread> handlers = new CopyOnWriteArrayList<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    // -------------------------------------------------------
    // Dodaj tu własne metody biznesowe, np. broadcast(), saveToDB(), itp.
    // -------------------------------------------------------

    public void broadcast(String message) {
        System.out.println("Serwer odebrał: " + message);
        for (ClientThread ct : handlers) {
            ct.send(message);
        }
    }

    public void removeHandler(ClientThread ct) {
        handlers.remove(ct);
    }

    // Wysyła wiadomość do wszystkich i czyści listę (wywoływane przez shutdown hook)
    public void disconnectHandlers() {
        handlers.forEach(handler -> handler.send("disconnected"));
        handlers.clear();
    }

    // Główna pętla – blokuje na accept(), każdy klient dostaje własny wątek
    public void listen() throws IOException {
        System.out.println("Server started");
        while (true) {
            System.out.println("Waiting for connection...");
            Socket socket = serverSocket.accept();
            ClientThread ct = new ClientThread(socket, this);
            Thread thread = new Thread(ct);
            thread.start();
            handlers.add(ct);
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);

        // Shutdown hook: uruchamiany przy Ctrl+C lub kill – grzecznie rozłącza klientów
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.disconnectHandlers();
        }));

        server.listen();
    }
}
