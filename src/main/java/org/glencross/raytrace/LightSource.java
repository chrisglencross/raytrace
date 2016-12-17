package org.glencross.raytrace;

public class LightSource {

    private Colour colour;
    private Vector location;

    public LightSource(Colour colour, Vector location) {
        this.colour = colour;
        this.location = location;
    }

    public Colour getColour() {
        return colour;
    }

    public Vector getLocation() {
        return location;
    }

}
