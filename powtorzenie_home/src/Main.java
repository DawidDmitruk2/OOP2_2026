import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Point> pts = new ArrayList<>();
        pts.add(new Point(0, 0));
        pts.add(new Point(0, 3));
        pts.add(new Point(4, 0));

        Polygon p = new Polygon(pts);
        System.out.println("Obwód: " + p.getPerimeter());

        List<Clock> clocks = new ArrayList<>();
        clocks.add(new DigitalClock(12,0,0));
        for (Clock c : clocks) {
            c.tick();
        }

        System.out.println(clocks);
    }
}