package pl.umcs.oop.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;

    public ClientThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true); // true = auto-flush
        this.server = server;
    }

    // Wysyła wiadomość do tego konkretnego klienta
    public void send(String message) {
        writer.println(message);
    }

    // Zamyka gniazdo i usuwa się z listy aktywnych połączeń
    private void close() throws IOException {
        socket.close();
        server.removeHandler(this);
    }

    @Override
    public void run() {
        System.out.println("Client connected");
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                // -------------------------------------------------------
                // Tu wstaw własną logikę obsługi wiadomości od klienta,
                // np. server.broadcast(message), parsowanie komend itp.
                // -------------------------------------------------------
                System.out.println("Odebrano od klienta: " + message);
                server.broadcast(message);
            }
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client disconnected");
    }
}
