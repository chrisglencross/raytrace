package org.glencross.raytrace;

public final class Vector {

    public static final Vector ZERO = new Vector(0, 0, 0);

    private final double x;
    private final double y;
    private final double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double scale() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public Vector toUnit() {
        double scale = scale();
        return new Vector(x/scale, y/scale, z/scale);
    }

    public double dotProduct(Vector other) {
        return this.x*other.x + this.y*other.y + this.z*other.z;
    }

    public Vector crossProduct(Vector other) {
        return new Vector(
                this.y*other.z-this.z*other.y,
                this.z*other.x-this.x*other.z,
                this.x*other.y-this.y*other.x);
    }

    public Vector minus(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector plus(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector mult(double scale) {
        return new Vector(this.x*scale, this.y*scale, this.z*scale);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        if (Double.compare(vector.x, x) != 0) return false;
        if (Double.compare(vector.y, y) != 0) return false;
        return Double.compare(vector.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
