package org.glencross.raytrace;

import java.util.List;
import java.util.stream.Collectors;

public class Triangle implements Shape {

    private final Vector p1;
    private final Vector p2;
    private final Vector p3;
    private final Plane plane;

    private final Vector cp2p1;
    private final Vector cp3p2;
    private final Vector cp1p3;

    private final BoundingBox boundingBox;

    public Triangle(Vector p1, Vector p2, Vector p3, Surface surface) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        /* Intersection with the triangle is performed by first checking whether the line
         * intersects the plane that the triangle is on, then checking whether the interesection
         * point is within the triangle.
         */
        Vector normal = p2.minus(p1).crossProduct(p3.minus(p1)).toUnit();
        this.plane = new Plane(normal, p1, surface);

        // cp2p1 is the normal of triangle side p2->p1 pointing towards the interior of the triangle, etc.
        cp2p1 = p1.minus(p2).crossProduct(normal);
        cp3p2 = p2.minus(p3).crossProduct(normal);
        cp1p3 = p3.minus(p1).crossProduct(normal);

        this.boundingBox = new BoundingBox(p1, p1)
                .combine(new BoundingBox(p2, p2))
                .combine(new BoundingBox(p3, p3));
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        List<LineShapeIntersection> intersections = plane.intersections(line);
        return intersections.stream().filter(l -> inTriangle(l.getLocation())).collect(Collectors.toList());
    }

    /**
     * Returns true if the point (already known to be on the plane of the triangle) is within the triangle.
     */
    private boolean inTriangle(Vector point) {
        return gtZero(point.minus(p2).dotProduct(cp2p1)) &&
                gtZero(point.minus(p3).dotProduct(cp3p2)) &&
                gtZero(point.minus(p1).dotProduct(cp1p3));
    }

    private boolean gtZero(double value)  {
        // Compensate for rounding errors in calculations which result in ugly gaps between adjacent triangles
        return value >= -0.00001;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String toString() {
        return "Triangle[" + p1 + ", " + p2 + ", " + p3 + "]";
    }

}
