package org.glencross.raytrace;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Sphere implements Shape {

    private final Vector centre;
    private final double radius;
    private final Surface surfaceProperties;
    private final BoundingBox boundingBox;

    public Sphere(Vector centre, double radius, Surface surfaceProperties) {
        this.centre = centre;
        this.radius = radius;
        this.surfaceProperties = surfaceProperties;
        this.boundingBox = new BoundingBox(
                new Vector(centre.getX()-radius, centre.getY()-radius, centre.getZ()-radius),
                new Vector(centre.getX()+radius, centre.getY()+radius, centre.getZ()+radius)
        );
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {

        Vector l = line.getDirection();
        Vector o = line.getOrigin();

        Vector offset = o.minus(centre);
        double t1 = l.dotProduct(offset);
        double t2 = offset.scale();
        double t3 = t1*t1-t2*t2+radius*radius;
        if (t3 <= 0) {
            return Collections.emptyList();
        }

        double sqrt = Math.sqrt(t3);
        double d1 = 0-t1+sqrt;
        double d2 = 0-t1-sqrt;
        return DoubleStream.of(d1, d2)
                .mapToObj(distance -> {
                    Vector location = o.plus(l.mult(distance));
                    Vector surfaceNormal = location.minus(centre).toUnit();
                    return new LineShapeIntersection(line, this, distance, location, surfaceNormal, surfaceProperties);
                })
                .collect(Collectors.toList());

    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
