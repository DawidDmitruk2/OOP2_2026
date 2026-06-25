package pl.umcs.oop;

import pl.umcs.oop.client.ServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Przykładowy punkt wejścia klienta bez JavaFX.
// Przy JavaFX – zastąp tę klasę Controllerem i obsługą onConnectClicked().
public class ClientMain {

    public static void main(String[] args) throws IOException {
        ServerThread serverThread = new ServerThread("localhost", 5000);

        // Consumer – co zrobić z każdą wiadomością od serwera
        serverThread.setConsumer(message -> {
            // -------------------------------------------------------
            // Tu wstaw własną logikę, np.:
            // MyModel model = MyModel.fromMessage(message);
            // Platform.runLater(() -> rysujNaCanvas(model)); // przy JavaFX
            // -------------------------------------------------------
            System.out.println("Odebrano: " + message);
        });

        // setDaemon(true) – wątek zakończy się razem z main (lub oknem JavaFX)
        serverThread.setDaemon(true);
        serverThread.start();

        // Pętla wejścia ze stdin (w JavaFX zastąp obsługą zdarzeń UI)
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = stdin.readLine()) != null) {
            serverThread.send(line);
        }
    }
}
