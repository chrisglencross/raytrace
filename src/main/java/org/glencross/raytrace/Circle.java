package org.glencross.raytrace;

import java.util.List;
import java.util.stream.Collectors;

public class Circle implements Shape {

    private final Vector centre;
    private final Vector normal;
    private final double radius;
    private final Plane plane;

    private final BoundingBox boundingBox;

    public Circle(Vector centre, Vector normal, double radius, Surface surface) {
        this.centre = centre;
        this.normal = normal.toUnit();
        this.radius = radius;
        this.plane = new Plane(normal, centre, surface);

        // Could make this smaller - currently the same as for a sphere
        this.boundingBox = new BoundingBox(
                new Vector(centre.getX()-radius, centre.getY()-radius, centre.getZ()-radius),
                new Vector(centre.getX()+radius, centre.getY()+radius, centre.getZ()+radius)
        );
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        List<LineShapeIntersection> intersections = plane.intersections(line);
        return intersections.stream().filter(l -> inCircle(l.getLocation())).collect(Collectors.toList());
    }

    /**
     * Returns true if the point (already known to be on the plane of the circle) is within the circle.
     */
    private boolean inCircle(Vector point) {
        return point.minus(centre).scale() < radius;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String toString() {
        return "Circle[centre=" + centre +
                ", normal=" + normal + ", radius=" + radius + "]";
    }

}
