package pl.umcs.oop.server;

import pl.umcs.oop.game.Gesture;
import pl.umcs.oop.game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Player implements Runnable {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    private String login;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getLogin() {
        return login;
    }

    public void send(String message) {
        writer.println(message);
    }

    private void close() throws IOException {
        socket.close();
        server.removeHandler(this);
    }

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
                    if (message.equals("r") || message.equals("p") || message.equals("s")) {
                        makeGesture(Gesture.fromString(message));
                    }
                } else {
                    server.challengeToDuel(this, message);
                }
            }

            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}