package org.glencross.raytrace;

import java.util.ArrayList;
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
    private final Circle baseCircle;
    private final Circle topCircle;


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

        this.baseCircle = new Circle(base, direction, radius, surfaceProperties);
        this.topCircle = new Circle(base.plus(direction.mult(height)), direction, radius, surfaceProperties);
        boundingBox = baseCircle.getBoundingBox().combine(topCircle.getBoundingBox());
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {

        Vector e = transformPositionToLocalFrameOfReference(line.getOrigin());
        Vector d = transformDirectionToLocalFrameOfReference(line.getDirection()).toUnit();

        // a=xD2+yD2, b=2xExD+2yEyD, and c=xE2+yE2-1.
        double a = d.getX()*d.getX() +
                   d.getZ()*d.getZ();
        double b = 2*e.getX()*d.getX() +
                   2*e.getZ()*d.getZ();
        double c = e.getX()*e.getX() +
                   e.getZ()*e.getZ() - 1;

        double b24ac = b*b-4*a*c;
        if (b24ac < 0) {
            return Collections.emptyList();
        }
        double s = Math.sqrt(b24ac);
        double t1 = (-b+s)/(2*a);
        double t2 = (-b-s)/(2*a);

        List<LineShapeIntersection> cylinderSideIntersections = DoubleStream.of(t1, t2)
                .filter(t -> {
                    double h = (e.getY() + d.getY() * t)*radius;
                    return (h >= 0 && h < height);
                })
                .mapToObj(distance -> {
                    Vector location = e.plus(d.mult(distance));
                    Vector surfaceNormal = new Vector(location.getX(), 0, location.getZ()).toUnit();

                    Vector transformedLocation = transformPositionFromLocalFrameOfReference(location);
                    Vector transformedNormal = transformDirectionFromLocalFrameOfReference(surfaceNormal).toUnit();
                    return new LineShapeIntersection(line, this, distance * radius,
                            transformedLocation, transformedNormal, surfaceProperties);
                })
                .collect(Collectors.toList());

        List<LineShapeIntersection> result = new ArrayList<>();
        result.addAll(cylinderSideIntersections);
        result.addAll(topCircle.intersections(line));
        result.addAll(baseCircle.intersections(line));
        return result;
    }

    private Vector transformPositionToLocalFrameOfReference(Vector position) {
        return inverseRotationMatrix.multiply(position.minus(this.base)).mult(1/radius);
    }

    private Vector transformDirectionToLocalFrameOfReference(Vector direction) {
        return inverseRotationMatrix.multiply(direction);
    }

    private Vector transformPositionFromLocalFrameOfReference(Vector position) {
        return rotationMatrix.multiply(position.mult(radius)).plus(this.base);
    }

    private Vector transformDirectionFromLocalFrameOfReference(Vector direction) {
        return rotationMatrix.multiply(direction);
    }


    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
