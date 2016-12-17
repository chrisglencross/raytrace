package org.glencross.raytrace;

public class LightSource {

    private final double brightness;
    private Colour colour;
    private Vector location;

    public LightSource(Colour colour, Vector location, double brightness) {
        this.colour = colour;
        this.location = location;
        this.brightness = brightness;
    }

    public Colour getColour() {
        return colour;
    }

    public Vector getLocation() {
        return location;
    }

    public double getBrightness() {
        return brightness;
    }
}
