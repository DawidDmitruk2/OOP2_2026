public class Vec3D {
    public double r;
    public double g;
    public double b;

    public Vec3D(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    protected Vec3D add(Vec3D other) {
        return new Vec3D(this.r + other.r, this.g + other.g, this.b + other.b);
    }

    protected Vec3D mul(double skalar) {
        return new Vec3D(this.r * skalar, this.g * skalar, this.b * skalar);
    }
}
