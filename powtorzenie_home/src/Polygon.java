import java.util.List;
import java.util.ArrayList;

public class Polygon {
    private List<Point> points; // Pola prywatne - enkapsulacja

    public Polygon(List<Point> points) {
        // Nasz wyjątek z kroku 3
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException("Wielokąt musi mieć min. 3 punkty!");
        }
        this.points = points;
    }

    public double getPerimeter() {
        double sum = 0;
        for (int i = 0; i < points.size(); i++) {
            Point current = points.get(i);
            Point next = points.get((i + 1) % points.size());
            sum += current.distance(next);
        }
        return sum;
    }
}