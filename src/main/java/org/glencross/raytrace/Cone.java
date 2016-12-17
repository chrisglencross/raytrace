package org.glencross.raytrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Cone implements Shape {

    private final Vector apex;
    private final Vector direction;
    private final double radiusDividedByHeight;
    private final double height;

    private final Surface surfaceProperties;
    private final BoundingBox boundingBox;
    private final Matrix rotationMatrix;
    private final Matrix inverseRotationMatrix;
    private final Circle baseCircle;

    public Cone(Vector apex, Vector direction, double radius, double height,
                Surface surfaceProperties) {
        direction = direction.toUnit();

        Vector d0 = new Vector(0, 0, 1);
        rotationMatrix = Matrix.getRotationMatrix(d0, direction);
        inverseRotationMatrix =  Matrix.getRotationMatrix(direction, d0);

        this.apex = apex;
        this.direction = direction;
        this.radiusDividedByHeight = radius/height;
        this.height = height;
        this.surfaceProperties = surfaceProperties;

        this.baseCircle = new Circle(apex.plus(direction.mult(height)),
                direction, radius, surfaceProperties);
        boundingBox = baseCircle.getBoundingBox().combine(new BoundingBox(apex, apex));
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {

        Vector e = transformPositionToLocalFrameOfReference(line.getOrigin());
        Vector d = transformDirectionToLocalFrameOfReference(line.getDirection()).toUnit();

        // a=xD2+yD2-zD2, b=2xExD+2yEyD-2zEzD, and c=xE2+yE2-zE2
        double a = d.getX()*d.getX() +
                   d.getY()*d.getY() -
                   d.getZ()*d.getZ()*radiusDividedByHeight*radiusDividedByHeight;
        double b = 2*e.getX()*d.getX() +
                   2*e.getY()*d.getY() -
                   2*e.getZ()*d.getZ()*radiusDividedByHeight*radiusDividedByHeight;
        double c = e.getX()*e.getX() +
                   e.getY()*e.getY() -
                   e.getZ()*e.getZ()*radiusDividedByHeight*radiusDividedByHeight;

        double b24ac = b*b-4*a*c;
        if (b24ac < 0) {
            return Collections.emptyList();
        }
        double s = Math.sqrt(b24ac);
        double t1 = (-b+s)/(2*a);
        double t2 = (-b-s)/(2*a);

        List<LineShapeIntersection> coneIntersections = DoubleStream.of(t1, t2)
                .filter(t -> t >= 0)
                .filter(t -> {
                    double h = e.getZ() + d.getZ() * t;
                    return (h >= 0 && h < height);
                })
                .mapToObj(distance -> {
                    Vector location = e.plus(d.mult(distance));
                    Vector toCentre = new Vector(location.getX(), location.getY(), 0);
                    Vector surfaceNormal = new Vector(toCentre.getX(), toCentre.getY(), -toCentre.scale() * radiusDividedByHeight).toUnit();

                    Vector transformedLocation = transformPositionFromLocalFrameOfReference(location);
                    Vector transformedNormal = transformDirectionFromLocalFrameOfReference(surfaceNormal).toUnit();

                    return new LineShapeIntersection(line, this,
                            distance, // Doesn't need scaling
                            transformedLocation,
                            transformedNormal,
                            surfaceProperties);
                })
                .collect(Collectors.toList());

        List<LineShapeIntersection> result = new ArrayList<>();
        result.addAll(coneIntersections);
        result.addAll(baseCircle.intersections(line));
        return result;
    }

    private Vector transformPositionToLocalFrameOfReference(Vector position) {
        return inverseRotationMatrix.multiply(position.minus(this.apex));
    }

    private Vector transformDirectionToLocalFrameOfReference(Vector direction) {
        return inverseRotationMatrix.multiply(direction);
    }

    private Vector transformPositionFromLocalFrameOfReference(Vector position) {
        return rotationMatrix.multiply(position).plus(this.apex);
    }

    private Vector transformDirectionFromLocalFrameOfReference(Vector direction) {
        return rotationMatrix.multiply(direction);
    }


    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
