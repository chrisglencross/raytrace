package org.glencross.raytrace;

public class Matrix {

    public static Matrix rotateAroundYAxis(double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Matrix(
                cos, 0, sin,
                0, 1, 0,
                0-sin, 0, cos
        );
    }

    public static Matrix rotateAroundXAxis(double radians) {
        double cos = Math.cos(-radians);
        double sin = Math.sin(-radians);
        return new Matrix(
                1, 0, 0,
                0, cos, 0-sin,
                0, sin, cos
        );
    }

    /**
     * Creates a rotation matrix that is able to rotate from one vector to another vector.
     *
     * From https://math.stackexchange.com/questions/293116/rotating-one-3d-vector-to-another
     */
    public static Matrix getRotationMatrix(Vector fromVector, Vector toVector) {
        Vector x = fromVector.crossProduct(toVector).toUnit();
        double theta = Math.acos(fromVector.dotProduct(toVector));
        if (theta <= 0.0001) {
            return Matrix.identity();
        }
        Matrix matrixA = new Matrix(
                0,          -x.getZ(),  x.getY(),
                x.getZ(),   0,          -x.getX(),
                -x.getY(),  x.getX(),   0
        );
        return Matrix.identity()
                .plus(matrixA.multiply(Math.sin(theta)))
                .plus(matrixA.multiply(matrixA).multiply(1-Math.cos(theta)));
    }


    public static Matrix identity() {
            return new Matrix(
                    1, 0, 0,
                    0, 1, 0,
                    0, 0, 1
            );
    }

    private final double x1;
    private final double x2;
    private final double x3;
    private final double y1;
    private final double y2;
    private final double y3;
    private final double z1;
    private final double z2;
    private final double z3;

    public Matrix(double x1, double x2, double x3,
                  double y1, double y2, double y3,
                  double z1, double z2, double z3) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.z1 = z1;
        this.z2 = z2;
        this.z3 = z3;
    }

    public Vector multiply(Vector vector) {
        return new Vector(
                x1*vector.getX()+x2*vector.getY()+x3*vector.getZ(),
                y1*vector.getX()+y2*vector.getY()+y3*vector.getZ(),
                z1*vector.getX()+z2*vector.getY()+z3*vector.getZ());
    }

    public String toString() {
        return String.format("(%f, %f, %f)\n(%f, %f, %f)\n(%f, %f, %f)",
                new Object[] {x1, x2, x3, y1, y2, y3, z1, z2, z3});
    }

    public Matrix multiply(Matrix o) {
        return new Matrix(
                x1*o.x1+x2*o.y1+x3*o.z1, x1*o.x2+x2*o.y2+x3*o.z2, x1*o.x3+x2*o.y3+x3*o.z3,
                y1*o.x1+y2*o.y1+y3*o.z1, y1*o.x2+y2*o.y2+y3*o.z2, y1*o.x3+y2*o.y3+y3*o.z3,
                z1*o.x1+z2*o.y1+z3*o.z1, z1*o.x2+z2*o.y2+z3*o.z2, z1*o.x3+z2*o.y3+z3*o.z3
        );
    }

    public Matrix multiply(double v) {
        return new Matrix(
                x1*v, x2*v, x3*v,
                y1*v, y2*v, y3*v,
                z1*v, z2*v, z3*v
        );
    }

    public Matrix plus(Matrix o) {
        return new Matrix(
                x1+o.x1, x2+o.x2, x3+o.x3,
                y1+o.y1, y2+o.y2, y3+o.y3,
                z1+o.z1, z2+o.z2, z3+o.z3
        );
    }

}
