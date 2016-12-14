package org.glencross.raytrace;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Cylinder implements Shape {

    private final Vector base;
    private final Vector direction;
    private final double radius;
    private final double height;
    private final Surface surfaceProperties;
    private final BoundingBox boundingBox;
    private final Matrix rotationMatrix;
    private final Matrix inverseRotationMatrix;


    public Cylinder(Vector base, Vector direction, double radius, double height,
                    Surface surfaceProperties) {
        direction = direction.toUnit();

        Vector d0 = new Vector(0, 1, 0);
        rotationMatrix = Matrix.getRotationMatrix(d0, direction);
        inverseRotationMatrix =  Matrix.getRotationMatrix(direction, d0);

        this.base = base;
        this.direction = direction;
        this.radius = radius;
        this.height = height;
        this.surfaceProperties = surfaceProperties;
//        Vector top = base.plus(direction.mult(height));
//        this.boundingBox = new BoundingBox(
//                new Vector(
//                        Math.min(base.getX(), top.getX()) - radius,
//                        Math.min(base.getY(), top.getY()) - radius,
//                        Math.min(base.getZ(), top.getZ()) - radius
//                ), new Vector(
//                Math.max(base.getX(), top.getX()) + radius,
//                Math.max(base.getY(), top.getY()) + radius,
//                Math.max(base.getZ(), top.getZ()) + radius
//        ));
        boundingBox = null;
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {

        Vector l = inverseRotationMatrix.multiply(line.getDirection());
        Vector o = inverseRotationMatrix.multiply(line.getOrigin().minus(this.base)).mult(1/radius);

        // TODO - rotate, scale and translate the line to match the unit cylinder

        // a=xD2+yD2, b=2xExD+2yEyD, and c=xE2+yE2-1.
        double a = l.getX()*l.getX() +
                   l.getZ()*l.getZ();

        double b = 2*o.getX()*l.getX() +
                   2*o.getZ()*l.getZ();

        double c = o.getX()*o.getX() +
                   o.getZ()*o.getZ() - 1;

        double b24ac = b*b-4*a*c;
        if (b24ac < 0.0001) {
            return Collections.emptyList();
        }
        double s = Math.sqrt(b24ac);
        double t1 = (-b+s)/(2*a);
        double t2 = (-b-s)/(2*a);

        return DoubleStream.of(t1, t2)
                .mapToObj(distance -> {
                    Vector location = rotationMatrix.multiply(o.plus(l.mult(distance)).mult(radius)).plus(this.base);
                    // the surface normal isn't right
                    Vector surfaceNormal = rotationMatrix.multiply(location.minus(new Vector(0, location.getY(), 0)));
                    return new LineShapeIntersection(line, this, distance, location, surfaceNormal, surfaceProperties);
                })
                // TODO - rotate, scale and translate the line back the other way
                .collect(Collectors.toList());

    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
