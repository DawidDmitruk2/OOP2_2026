package pl.umcs.oop.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

// Wątek klienta obsługujący połączenie z serwerem.
// Nazwa "ServerThread" oznacza: wątek odpowiedzialny za komunikację z serwerem.
public class ServerThread extends Thread {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    // Callback wywoływany dla każdej odebranej wiadomości.
    // Typ Consumer<String> – zamień na Consumer<TwojModel> gdy masz własny model danych.
    private Consumer<String> consumer;

    public void setConsumer(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    // Nawiązuje połączenie TCP z serwerem przy tworzeniu obiektu
    public ServerThread(String address, int port) throws IOException {
        socket = new Socket(address, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true); // true = auto-flush
    }

    // Wysyła wiadomość do serwera
    public void send(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        System.out.println("Połączono z serwerem");
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("disconnected")) {
                    System.out.println("Server disconnected!");
                    break;
                }
                // -------------------------------------------------------
                // Tu wstaw własną logikę obsługi wiadomości od serwera,
                // np. deserializację modelu i wywołanie consumer.accept(...)
                // -------------------------------------------------------
                if (consumer != null) {
                    consumer.accept(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
