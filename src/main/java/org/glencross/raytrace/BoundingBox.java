package org.glencross.raytrace;

public class BoundingBox {

    private static final Vector MAX = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    private static final Vector MIN = Vector.ZERO.minus(MAX);

    /** Bounding box of infinite or unknown size */
    public static final BoundingBox INFINITE = new BoundingBox(MIN, MAX);

    /** Bounding box of zero size */
    public static final BoundingBox NONE = new BoundingBox(MAX, MIN);

    private final Vector min;
    private final Vector max;

    public BoundingBox(Vector min, Vector max) {
        this.min = min;
        this.max = max;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public BoundingBox combine(BoundingBox other) {
        return new BoundingBox(
                new Vector(
                        Math.min(this.min.getX(), other.min.getX()),
                        Math.min(this.min.getY(), other.min.getY()),
                        Math.min(this.min.getZ(), other.min.getZ())),
                new Vector(
                        Math.max(this.max.getX(), other.max.getX()),
                        Math.max(this.max.getY(), other.max.getY()),
                        Math.max(this.max.getZ(), other.max.getZ())));
    }

    public boolean intersects(Line line) {

        double tmin = -Double.MAX_VALUE;
        double tmax = Double.MAX_VALUE;

        Vector origin = line.getOrigin();
        Vector direction = line.getDirection();
        if (direction.getX() != 0.0) {
            double tx1 = (min.getX() - origin.getX())/direction.getX();
            double tx2 = (max.getX() - origin.getX())/direction.getX();
            tmin = Math.max(tmin, Math.min(tx1, tx2));
            tmax = Math.min(tmax, Math.max(tx1, tx2));
        }
        if (direction.getY() != 0.0) {
            double ty1 = (min.getY() - origin.getY())/direction.getY();
            double ty2 = (max.getY() - origin.getY())/direction.getY();
            tmin = Math.max(tmin, Math.min(ty1, ty2));
            tmax = Math.min(tmax, Math.max(ty1, ty2));
        }
        if (direction.getZ() != 0.0) {
            double tz1 = (min.getZ() - origin.getZ())/direction.getZ();
            double tz2 = (max.getZ() - origin.getZ())/direction.getZ();
            tmin = Math.max(tmin, Math.min(tz1, tz2));
            tmax = Math.min(tmax, Math.max(tz1, tz2));
        }

        return tmax >= tmin;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    public boolean intersects(BoundingBox other) {
        if (this.max.getX() < other.min.getX()) return false;
        if (this.min.getX() > other.max.getX()) return false;
        if (this.max.getY() < other.min.getY()) return false;
        if (this.min.getY() > other.max.getY()) return false;
        if (this.max.getZ() < other.min.getZ()) return false;
        if (this.min.getZ() > other.max.getZ()) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoundingBox box = (BoundingBox) o;

        if (!min.equals(box.min)) return false;
        return max.equals(box.max);
    }

    @Override
    public int hashCode() {
        int result = min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }
}
