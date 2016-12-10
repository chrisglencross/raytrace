package org.glencross.raytrace;

public class Surface {

    private final Colour colour;
    private final double reflectivity;
    // Transparency etc?

    public Surface(Colour colour, double reflectivity) {
        this.colour = colour;
        this.reflectivity = reflectivity;
    }

    public Colour getColour() {
        return colour;
    }

    public double getReflectivity() {
        return reflectivity;
    }
}
