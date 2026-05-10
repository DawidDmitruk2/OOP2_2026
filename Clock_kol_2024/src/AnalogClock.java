import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalogClock extends Clock {

    public AnalogClock(City city) {
        super(city);
    }

    public void toSvg(String path) throws IOException {
        String svg =
                "<svg width=\"200\" height=\"200\" viewBox=\"-100 -100 200 200\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                        "  <!-- Tarcza zegara -->\n" +
                        "  <circle cx=\"0\" cy=\"0\" r=\"90\" fill=\"none\" stroke=\"black\" stroke-width=\"2\" />\n" +
                        "</svg>";
        Files.writeString(Paths.get(path), svg);
    }
}
