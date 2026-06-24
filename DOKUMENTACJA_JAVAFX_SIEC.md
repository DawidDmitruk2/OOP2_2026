# Dokumentacja: JavaFX + Sieć TCP + SQLite

> Dotyczy projektu **CircleApp** (zadania 1–5) oraz analogicznego projektu (Kamień-Papier-Nożyce).  
> Kod oznaczony `// [z PDF]` można skopiować bezpośrednio ze slajdów wykładu.

---

## Architektura całego systemu

```
[Klient A (JavaFX)]
    └─ Controller
         └─ ServerThread (wątek klienta)
              │  send("x y kolor promień")
              ▼
         [Serwer TCP]
              └─ Server
                   ├─ ClientThread A (wątek dla klienta A)
                   ├─ ClientThread B (wątek dla klienta B)
                   └─ broadcast() → wysyła do WSZYSTKICH
              ▼
[Klient B (JavaFX)]
    └─ Controller
         └─ ServerThread (wątek klienta)
              └─ consumer → rysuje kółko na Canvas
```

**Przepływ danych:**
1. Użytkownik klika na Canvas → `Controller.onMouseClicked()`
2. Dane koła → `Dot.toMessage()` → string przez TCP
3. Serwer odbiera → `broadcast()` → wysyła do wszystkich klientów
4. Każdy klient odbiera → `Dot.fromMessage()` → `consumer.accept(dot)` → rysuje

---

## Zadanie 1 – Rysowanie kół na Canvas (JavaFX)

### Jak działa Canvas w JavaFX

```java
// Pobranie kontekstu graficznego z pola @FXML canvas
canvas.getGraphicsContext2D().setFill(color);    // ustawia kolor wypełnienia
canvas.getGraphicsContext2D().fillOval(
    x - radius,   // lewy górny róg prostokąta opisanego na owalu
    y - radius,
    radius * 2,   // szerokość
    radius * 2    // wysokość
);
// fillOval rysuje elipsę wpisaną w prostokąt; środek = (x, y) gdy offset = radius
```

### Obsługa kliknięcia myszy

```java
@FXML
protected void onMouseClicked(MouseEvent mouseEvent) {
    // Filtrujemy: tylko kliknięcie LPM na samym canvas (nie na kontrolkach)
    if (mouseEvent.getTarget() == canvas
            && mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED
            && mouseEvent.getButton() == MouseButton.PRIMARY) {

        double x = mouseEvent.getX();          // pozycja kursora na canvas
        double y = mouseEvent.getY();
        Color color = colorPicker.getValue();  // aktualny kolor z ColorPicker
        double radius = radiusSlider.getValue(); // promień z Slider
        // ... dalsze działanie (zadanie 4)
    }
}
```

### Kontrolki FXML które musisz podpiąć

| Pole w Controller | Typ JavaFX | Co daje |
|---|---|---|
| `colorPicker` | `ColorPicker` | `getValue()` → `Color` |
| `radiusSlider` | `Slider` | `getValue()` → `double` |
| `canvas` | `Canvas` | `getGraphicsContext2D()` |
| `addressField` | `TextField` | `getText()` → adres IP |
| `portField` | `TextField` | `Integer.parseInt(getText())` → port |

---

## Zadanie 2 – Struktura pakietów: Server i Client

### Pakiet `server` – klasa `Server`

```java
// [z PDF – można skopiować szkielet]
public class Server {
    private final ServerSocket serverSocket;
    private CopyOnWriteArrayList<ClientThread> handlers = new CopyOnWriteArrayList<>();
    // CopyOnWriteArrayList zamiast ArrayList – bezpieczny przy wielu wątkach
    // (broadcast iteruje po liście, gdy inne wątki mogą ją modyfikować)

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void broadcast(String message) {
        for (ClientThread ct : handlers) {
            ct.send(message);  // wysyła do każdego podłączonego klienta
        }
    }

    public void listen() throws IOException {
        System.out.println("Server started");
        while (true) {
            Socket socket = serverSocket.accept(); // blokuje do momentu połączenia
            ClientThread ct = new ClientThread(socket, this);
            Thread thread = new Thread(ct);
            thread.start();       // każdy klient w osobnym wątku
            handlers.add(ct);
        }
    }
}
```

### Pakiet `server` – klasa `ClientThread`

```java
// [z PDF – można skopiować szkielet]
public class ClientThread implements Runnable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;

    public ClientThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        // true = auto-flush po println()
        this.server = server;
    }

    public void send(String message) {
        writer.println(message); // wysyła linię do tego konkretnego klienta
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                server.broadcast(message); // przesyła do wszystkich
            }
            close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() throws IOException {
        socket.close();
        server.removeHandler(this); // usuwa się z listy aktywnych połączeń
    }
}
```

### Pakiet `client` – klasa `ServerThread`

> Nazwa myląca – to wątek **klienta**, który obsługuje połączenie z serwerem.

```java
public class ServerThread extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private Consumer<Dot> consumer; // callback – co zrobić z odebranym kółkiem

    public ServerThread(String address, int port) throws IOException {
        socket = new Socket(address, port); // nawiązuje połączenie z serwerem
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setConsumer(Consumer<Dot> consumer) {
        this.consumer = consumer;
    }

    public void send(String message) {
        writer.println(message); // wysyła dane kółka do serwera
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("disconnected")) {
                    System.out.println("Server disconnected!");
                } else {
                    Dot dot = Dot.fromMessage(message);
                    consumer.accept(dot); // wywołuje callback z odebranym kółkiem
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Dlaczego `setDaemon(true)` na ServerThread?

```java
serverThread.setDaemon(true);
serverThread.start();
// Wątek-demon kończy się automatycznie gdy główny wątek (JavaFX) się zamknie.
// Bez tego aplikacja JavaFX nie zamknie się po kliknięciu X w oknie.
```

---

## Zadanie 3 – Rekord `Dot` z serializacją sieciową

```java
// record to skrócony sposób zapisu klasy z finalnymi polami i automatycznym
// konstruktorem, getterami (x(), y(), ...), equals(), hashCode(), toString()
public record Dot(double x, double y, Color color, double radius) {

    // Serializacja: zamienia obiekt na string do wysłania przez sieć
    public String toMessage() {
        return String.format(Locale.ENGLISH, "%f %f %s %f", x, y, color.toString(), radius);
        // Locale.ENGLISH ważne! Bez tego na systemach polskich "." zamieniłoby się na ","
        // co zepsułoby parsowanie po drugiej stronie
        // color.toString() daje np. "0x4169e1ff" (hex RGBA)
    }

    // Deserializacja: odtwarza obiekt z stringa odebranego z sieci
    public static Dot fromMessage(String message) {
        String[] parts = message.split(" ");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        Color c = Color.valueOf(parts[2]);  // JavaFX Color.valueOf() rozumie "0x4169e1ff"
        double r = Double.parseDouble(parts[3]);
        return new Dot(x, y, c, r);
    }
}
```

**Format wiadomości:** `"150.000000 200.000000 0x4169e1ff 25.000000"`

---

## Zadanie 4 – Podpięcie serwera do kontrolera

### Połączenie w `onConnectClicked()`

```java
@FXML
protected void onConnectClicked() {
    String host = addressField.getText();
    int port = Integer.parseInt(portField.getText());

    try {
        serverThread = new ServerThread(host, port);

        // Platform.runLater() – KONIECZNE przy aktualizacji UI z innego wątku!
        // JavaFX GUI można modyfikować tylko z wątku JavaFX (Application Thread).
        // ServerThread.run() działa w osobnym wątku → trzeba zakolejkować na wątek JavaFX.
        serverThread.setConsumer(dot -> Platform.runLater(() -> {
            canvas.getGraphicsContext2D().setFill(dot.color());
            canvas.getGraphicsContext2D().fillOval(
                    dot.x() - dot.radius(), dot.y() - dot.radius(),
                    dot.radius() * 2, dot.radius() * 2);
        }));

        serverThread.setDaemon(true); // zakończy się razem z oknem JavaFX
        serverThread.start();
    } catch (IOException e) {
        System.out.println("Błąd połączenia z serwerem: " + e.getMessage());
    }
}
```

### Wysyłanie w `onMouseClicked()`

```java
// Po kliknięciu – tworzony jest Dot i wysyłany jako string przez sieć
Dot dot = new Dot(x, y, color, radius);
serverThread.send(dot.toMessage());
// Serwer odbierze → broadcast → wróci do NAS i INNYCH klientów → consumer.accept()
// Dlatego nie rysujemy lokalnie – serwer odeśle nam nasze własne kółko
```

---

## Zadanie 5 – Baza danych SQLite w serwerze

### Schemat tabeli

```sql
CREATE TABLE dot(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    color TEXT NOT NULL,
    radius INTEGER NOT NULL
);
```

### Klasa Database (szablon)

```java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:dots.db";

    public Database() {
        // Przy starcie serwera tworzymy tabelę jeśli nie istnieje
        String sql = "CREATE TABLE IF NOT EXISTS dot(" +
                     "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                     "x INTEGER NOT NULL, y INTEGER NOT NULL," +
                     "color TEXT NOT NULL, radius INTEGER NOT NULL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Zapisuje pojedyncze kółko do bazy
    public void saveDot(Dot dot) {
        String sql = "INSERT INTO dot(x, y, color, radius) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, (int) dot.x());
            pstmt.setInt(2, (int) dot.y());
            pstmt.setString(3, dot.color().toString());
            pstmt.setInt(4, (int) dot.radius());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Zwraca listę wszystkich zapisanych kółek
    public List<Dot> getSavedDots() {
        List<Dot> dots = new ArrayList<>();
        String sql = "SELECT x, y, color, radius FROM dot";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dots.add(new Dot(
                    rs.getInt("x"), rs.getInt("y"),
                    javafx.scene.paint.Color.valueOf(rs.getString("color")),
                    rs.getInt("radius")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dots;
    }
}
```

### Modyfikacja `broadcast()` i `listen()` w Server

```java
// broadcast() – oprócz rozgłaszania, zapisuje kółko do bazy
public void broadcast(String message) {
    database.saveDot(Dot.fromMessage(message)); // zapis do bazy
    for (ClientThread ct : handlers) {
        ct.send(message);
    }
}

// listen() – po podłączeniu nowego klienta, wysyła mu wszystkie istniejące kółka
public void listen() throws IOException {
    while (true) {
        Socket socket = serverSocket.accept();
        ClientThread ct = new ClientThread(socket, this);
        Thread thread = new Thread(ct);
        thread.start();
        handlers.add(ct);

        // Wysyłamy nowemu klientowi historię kółek z bazy
        for (Dot dot : database.getSavedDots()) {
            ct.send(dot.toMessage()); // pomocnicza metoda toMessage() na instancji
        }
    }
}
```

> `Dot` jest rekordem, więc `dot.toMessage()` działa bez zmian.

---

## Shutdown hook – zamykanie połączeń przy Ctrl+C

```java
// [z PDF – można skopiować]
public static void main(String[] args) throws IOException {
    Server server = new Server(5000);

    // Rejestrujemy akcję wykonywaną przy zamknięciu JVM (Ctrl+C, kill itp.)
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        server.disconnectHandlers(); // wysyła "disconnected" do wszystkich i czyści listę
    }));

    server.listen();
}

// disconnectHandlers() w Server:
public void disconnectHandlers() {
    handlers.forEach(handler -> handler.send("disconnected"));
    handlers.clear();
}
```

---

## Analogia do projektu Kamień-Papier-Nożyce

| Element | CircleApp | Kamień-Papier-Nożyce |
|---|---|---|
| Dane przesyłane siecią | `Dot` (x, y, kolor, promień) | `Gesture` (r/p/s) + login |
| Serializacja | `Dot.toMessage()` / `fromMessage()` | string "r", "p", "s" |
| Handler klienta | `ClientThread` | `ClientHandler extends Player` |
| Routing wiadomości | `broadcast()` do wszystkich | logika pojedynku (`Duel`) |
| Baza danych | `dot` (historia kółek) | `users` (login, hasło, punkty) |
| Callback po zdarzeniu | `Consumer<Dot>` w `ServerThread` | `Runnable onEnd` w `Duel` |
| Wątek UI | `Platform.runLater()` | brak (brak GUI) |

### Co jest identyczne strukturalnie

**Serwer wielowątkowy** – identyczny schemat w obu projektach:
```java
// [z PDF – identyczny szkielet]
while (true) {
    Socket socket = serverSocket.accept();
    XxxHandler handler = new XxxHandler(socket, this);
    new Thread(handler).start();
    handlers.add(handler);
}
```

**Usuwanie rozłączonego klienta:**
```java
// [z PDF – identyczny wzorzec]
private void close() throws IOException {
    socket.close();
    server.removeHandler(this);
}
```

**Strumienie wejścia/wyjścia** – identyczne w obu projektach:
```java
// [z PDF – można skopiować]
reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
writer = new PrintWriter(socket.getOutputStream(), true);
```

---

## Najczęstsze błędy i jak ich unikać

| Problem | Przyczyna | Rozwiązanie |
|---|---|---|
| `IllegalStateException: Not on FX application thread` | Modyfikacja GUI spoza wątku JavaFX | Zawsze owijaj w `Platform.runLater(() -> {...})` |
| Liczby z przecinkiem zamiast kropki | Brak `Locale.ENGLISH` w `String.format` | Dodaj `Locale.ENGLISH` jako 1. argument |
| `ConcurrentModificationException` na liście handlerów | `ArrayList` nie jest thread-safe | Użyj `CopyOnWriteArrayList` |
| Aplikacja nie zamyka się po kliknięciu X | Wątek nie jest demonem | `serverThread.setDaemon(true)` przed `start()` |
| `Color.valueOf()` rzuca wyjątek | Niepoprawny format koloru | Upewnij się że `color.toString()` daje hex np. `0x4169e1ff` |
| Klient nie widzi starych kółek po połączeniu | Brak wysyłania historii | Po `handlers.add(ct)` iteruj po `getSavedDots()` i wysyłaj |

---

## Kolejność implementacji (zalecana)

1. **`Dot.java`** – rekord + `toMessage()` + `fromMessage()` (nie wymaga sieci)
2. **`Server.java`** + **`ClientThread.java`** – serwer bez DB, tylko `broadcast()`
3. **`ServerThread.java`** – klient, połączenie + pętla odbioru
4. **`Controller.java`** – podpięcie `ServerThread`, `Platform.runLater`, rysowanie
5. **`Database.java`** – SQLite, `saveDot()`, `getSavedDots()`
6. Modyfikacja `Server` – zapis w `broadcast()` + wysyłka historii w `listen()`

---

## Uruchomienie

```bash
# Terminal 1 – uruchom serwer
cd <katalog projektu>
java -cp .:sqlite-jdbc-*.jar pl.umcs.oop.circleapp.server.Server

# Terminal 2, 3, ... – uruchom klientów (JavaFX)
# W IntelliJ: Run → Launcher
# W polu "Address": localhost, Port: 5000
# Kliknij Connect, potem klikaj na canvas
```
