package org.glencross.raytrace;

public class LineShapeIntersection {

    private final Shape shape;
    private final Vector location;
    private final Vector surfaceNormal;
    private final Surface surfaceProperties;
    private final double distance;

    public LineShapeIntersection(Shape shape, double distance, Vector location, Vector surfaceNormal, Surface surfaceProperties) {
        this.shape = shape;
        this.distance = distance;
        this.location = location;
        this.surfaceNormal = surfaceNormal;
        this.surfaceProperties = surfaceProperties;
    }

    public Shape getShape() {
        return shape;
    }

    public Vector getLocation() {
        return location;
    }

    public Vector getSurfaceNormal() {
        return surfaceNormal;
    }

    public Surface getSurfaceProperties() {
        return surfaceProperties;
    }

    public double getDistance() {
        return distance;
    }
}
