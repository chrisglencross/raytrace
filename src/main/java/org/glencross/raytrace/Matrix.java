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

    public static void main(String[] args) {
        Vector v = new Vector(0, 1, 1);
        System.out.println(rotateAroundYAxis(Math.toRadians(10)).multiply(v));
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

}
