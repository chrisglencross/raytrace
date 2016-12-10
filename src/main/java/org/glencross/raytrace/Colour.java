package org.glencross.raytrace;

public class Colour {

    public static final Colour WHITE = new Colour(1, 1, 1);
    public static final Colour RED = new Colour(1, 0, 0);
    public static final Colour BLACK = new Colour(0, 0, 0);
    public static final Colour GREEN = new Colour(0, 1, 0);
    public static final Colour GREY = new Colour(0.7, 0.7, 0.7);
    public static final Colour BLUE = new Colour(0, 0, 1);
    public static final Colour CYAN = new Colour(0, 1, 1);
    public static final Colour YELLOW = new Colour(1, 1, 0);


    private double r;
    private double g;
    private double b;

    public Colour(double r, double g, double b) {
        this.r = Math.min(1d, r);
        this.g = Math.min(1d, g);
        this.b = Math.min(1d, b);
    }

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
    }

    public int toInt() {
        return (Math.min(255, (int)(r*255)) << 16) +
               (Math.min(255, (int)(g*255)) << 8) +
               (Math.min(255, (int)(b*255)));
    }

    public Colour times(Colour other) {
        return new Colour(other.r*r, other.g*g, other.b*b);
    }

    public Colour times(double factor) {
        return new Colour(r*factor, g*factor, b*factor);
    }

    public Colour add(Colour other) {
        return new Colour(other.r+r, other.g+g, other.b+b);
    }
}
