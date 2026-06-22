package pl.umcs.oop.circleapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final ServerSocket serverSocket;
    private CopyOnWriteArrayList<ClientThread> handlers = new CopyOnWriteArrayList<>();


    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void removeHandler(ClientThread ct) {
        handlers.remove(ct);
    }

    public void disconnectHandlers() {
        handlers.forEach(handler -> handler.send("disconnected"));
        handlers.clear();
    }

    public void broadcast(String message) {
        for (ClientThread ct : handlers) {
            ct.send(message);
        }
    }

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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.disconnectHandlers();
        }));

        server.listen();
    }
}
