public class Main {
    public static void main(String[] args) {
        ColorRGB red = new ColorRGB(1.0, 0.0, 0.0);
        ColorRGB green = new ColorRGB(0.0, 1.0, 0.0);
        double ratio = 0.5;
        System.out.println("Nowy kolor: " + ColorRGB.mix(red, green, ratio));
    }
}