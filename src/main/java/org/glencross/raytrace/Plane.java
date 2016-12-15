package org.glencross.raytrace;

import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 04/12/16.
 */
public class Plane implements Shape {

    private final Vector surfaceNormal;
    private final Surface surfaceProperties;
    private final Vector pointOnPlane;

    public Plane(Vector surfaceNormal, double distanceFromOrigin, Surface surfaceProperties) {
        this.surfaceNormal = surfaceNormal.toUnit();
        this.pointOnPlane = this.surfaceNormal.mult(distanceFromOrigin);
        this.surfaceProperties = surfaceProperties;
    }

    public Plane(Vector surfaceNormal, Vector pointOnPlane, Surface surfaceProperties) {
        this.surfaceNormal = surfaceNormal.toUnit();
        this.pointOnPlane = pointOnPlane;
        this.surfaceProperties = surfaceProperties;
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        double numerator = surfaceNormal.dotProduct(pointOnPlane.minus(line.getOrigin()));
        double denominator = surfaceNormal.dotProduct(line.getDirection());
        if (Math.abs(denominator) < 0.0001) {
            return Collections.emptyList();
        }
        double distance = numerator / denominator;
        if (distance < 0) {
            return Collections.emptyList();
        }
        Vector intersection = line.getOrigin().plus(line.getDirection().mult(distance));
        return Collections.singletonList(new LineShapeIntersection(line, this, distance,
                intersection,
                surfaceNormal, surfaceProperties));

    }
}
