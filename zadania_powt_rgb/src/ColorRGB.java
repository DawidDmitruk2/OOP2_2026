public class ColorRGB extends Vec3D{
    public ColorRGB(double r, double g, double b) {
        super(r, g, b);
    }

    public static ColorRGB mix(ColorRGB c1, ColorRGB c2, double ratio) {
        Vec3D mixed = c1.mul(ratio).add(c2.mul(1 - ratio));
        return new ColorRGB(mixed.r, mixed.g, mixed.b);
    }

    @Override
    public String toString() {
        return "ColorRGB{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
