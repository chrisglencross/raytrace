package org.glencross.raytrace;

import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 04/12/16.
 */
public class ChequeredPlane implements Shape {

    private final Vector surfaceNormal;
    private final Surface surfaceProperties1;
    private final Surface surfaceProperties2;

    private final double distanceFromOrigin;
    private final Vector pointOnPlane;
    private final double chequerSize;
    private final Vector planeVector2;
    private final Vector planeVector1;

    public ChequeredPlane(Vector surfaceNormal, double distanceFromOrigin, Surface surfaceProperties1,
                          Surface surfaceProperties2, double chequerSize) {
        this.surfaceNormal = surfaceNormal.toUnit();
        this.distanceFromOrigin = distanceFromOrigin;
        this.pointOnPlane = this.surfaceNormal.mult(distanceFromOrigin);
        Vector v = surfaceNormal.plus(new Vector(1000,0,0));
        if (v.dotProduct(surfaceNormal) < 0.00001) {
            v = surfaceNormal.plus(new Vector(0, -1000, 0));
        }
        this.planeVector2 = surfaceNormal.crossProduct(v).toUnit();
        this.planeVector1 = planeVector2.crossProduct(surfaceNormal).toUnit();
        this.surfaceProperties1 = surfaceProperties1;
        this.surfaceProperties2 = surfaceProperties2;
        this.chequerSize = chequerSize;
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        double numerator = surfaceNormal.dotProduct(pointOnPlane.minus(line.getOrigin()));
        double denominator = surfaceNormal.dotProduct(line.getDirection());
        if (denominator == 0) {
            return Collections.emptyList();
        }
        double distance = numerator / denominator;
        if (distance < 0) {
            return Collections.emptyList();
        }
        Vector intersection = line.getOrigin().plus(line.getDirection().mult(distance));
        double x = intersection.minus(pointOnPlane).dotProduct(planeVector1);
        double y = intersection.minus(pointOnPlane).dotProduct(planeVector2);
        int total = (int)((10000+x)/chequerSize) + (int)((10000+y)/chequerSize);
        return Collections.singletonList(new LineShapeIntersection(line, this, distance,
                intersection,
                surfaceNormal, (total % 2) == 0 ? surfaceProperties2 : surfaceProperties1));

    }
}
