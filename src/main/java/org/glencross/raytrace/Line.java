package org.glencross.raytrace;

public final class Line {

    private final Vector origin;
    private final Vector direction;

    public Line(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.toUnit();
    }

    public Vector getOrigin() {
        return origin;
    }

    public Vector getDirection() {
        return direction;
    }
}
