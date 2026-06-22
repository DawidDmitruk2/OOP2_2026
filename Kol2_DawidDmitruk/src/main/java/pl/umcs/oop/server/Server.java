package pl.umcs.oop.server;

import pl.umcs.oop.game.Duel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> handlers = new ArrayList<>();
    private Database database = new Database();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public Database getDatabase() {
        return database;
    }

    public void removeHandler(ClientHandler handler) {
        handlers.remove(handler);
    }

    public void send(ClientHandler handler, String message) {
        handler.send(message);
    }

    private void printLeaderboard() {
        System.out.println("Leaderboard");
        for (Map.Entry<String, Integer> entry : database.getLeaderboard().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


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

    private void startDuel(ClientHandler challenger, ClientHandler challengee) {
        Duel duel = new Duel(challenger, challengee);
        challenger.send("Pojedynek rozpoczety z: " + challengee.getLogin());
        challengee.send("Pojedynek rozpoczety z: " + challenger.getLogin());

        duel.setOnEnd(() -> {
            Duel.Result result = duel.evaluate();
            if (result == null) {
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

        public void listen() {
        System.out.println("Server started");
        while (true) {
            try {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                Thread thread = new Thread(handler);
                thread.start();
                handlers.add(handler);
            } catch (IOException e) {
                System.err.println("Error polaczenia: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
        server.listen();
    }
}