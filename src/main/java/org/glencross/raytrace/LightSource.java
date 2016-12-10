package org.glencross.raytrace;

public class LightSource {

    private Colour colour;
    private Vector location;
    private Vector direction;

    public LightSource(Colour colour, Vector location, Vector direction) {
        this.colour = colour;
        this.location = location;
        this.direction = direction;
    }

    public Colour getColour() {
        return colour;
    }

    public Vector getLocation() {
        return location;
    }

    public Vector getDirection() {
        return direction;
    }
}
