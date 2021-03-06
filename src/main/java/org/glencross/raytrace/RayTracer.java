package org.glencross.raytrace;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Created by chris on 08/12/16.
 */
public class RayTracer {

    private final Scene scene;
    private final int width;
    private final int height;

    public RayTracer(Scene scene, int pxWidth, int pxHeight) {
        this.scene = scene;
        this.width = pxWidth;
        this.height = pxHeight;
    }

    public RenderedImage render() {

        long startTimeMillis = System.currentTimeMillis();

        double horizontalFieldOfViewDegrees = scene.getHorizontalFieldOfViewDegrees();
        double verticalFieldOfViewDegrees = horizontalFieldOfViewDegrees / width * height;

        Vector screenCentreDirection = scene.getViewerDirection();
        double topAngle = Math.toRadians(verticalFieldOfViewDegrees/2);
        double leftAngle = Math.toRadians(-horizontalFieldOfViewDegrees/2);
        double pixelAngle = Math.toRadians(verticalFieldOfViewDegrees/height);

        Vector rightOfScreenVector = scene.getTopOfScreenDirection().crossProduct(scene.getViewerDirection()).toUnit();
        Vector bottomOfScreenVector = rightOfScreenVector.crossProduct(scene.getViewerDirection()).toUnit();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        List<Long> rowTimes = new ArrayList<>();

        IntStream.range(0, height).forEach(y -> {

            long rowStartTime = System.currentTimeMillis();
            Vector rowDirection = Matrix.rotateAroundVector(rightOfScreenVector, topAngle-y*pixelAngle).multiply(screenCentreDirection);
            IntStream.range(0, width).parallel().forEach(x -> {
                Vector pixelDirection = Matrix.rotateAroundVector(bottomOfScreenVector, leftAngle+x*pixelAngle).multiply(rowDirection).toUnit();
                Line line = new Line(scene.getViewerLocation(), pixelDirection);
                Colour colour = getColourOfLine(null, line, 5);
                int intColour = colour.toInt();
                image.setRGB(x, y, intColour);
            });

            long rowEndTime = System.currentTimeMillis();
            rowTimes.add(rowEndTime-rowStartTime);

            double completed = ((double)y)/height;

            List<Long> recentRows = rowTimes.size() > 10 ? rowTimes.subList(rowTimes.size()-10, rowTimes.size()) : rowTimes;
            double averageRowTime = recentRows.stream().mapToLong(Long::longValue).average().getAsDouble();
            long estimateRemaining = (long)(averageRowTime * (height-y));
            System.out.println(String.format("%.2f%% (estimated %d seconds remaining)", 100d*completed, estimateRemaining/1000));
        });


        long endTimeMillis = System.currentTimeMillis();

        System.out.println("Rendering took " + (endTimeMillis - startTimeMillis) + " milliseconds");

        return image;
    }

    private Colour getColourOfLine(Shape fromShape, Line line, int depthRemaining) {
        Optional<LineShapeIntersection> optionalIntersection = scene.getShapes().stream()
                .flatMap(shape -> shape.intersections(line).stream())
                .filter(i -> i.getShape() != fromShape)
                .filter(i -> i.getDistance() > 0)
                .sorted(Comparator.comparing(LineShapeIntersection::getDistance))
                .findFirst();
        Colour colour = scene.getAmbientLight();
        if (optionalIntersection.isPresent()) {
            LineShapeIntersection intersection = optionalIntersection.get();
            Colour ambientIllumination = getLightSourceIllumination(intersection);
            Colour reflectedIllumination = getReflectedIllumination(intersection, depthRemaining);
            Colour surfaceColour = intersection.getSurfaceProperties().getColour();
            colour = ambientIllumination.add(reflectedIllumination).times(surfaceColour);

            colour = applyFog(intersection.getDistance(), colour);
        }
        return colour;
    }

    private Colour getReflectedIllumination(LineShapeIntersection intersection, int depthRemaining) {
        Colour reflectedIllumination = Colour.BLACK;
        double reflectivity = intersection.getSurfaceProperties().getReflectivity();
        if (depthRemaining > 0 && reflectivity > 0) {
            Vector surfaceNormal = intersection.getSurfaceNormal();
            Vector lineDirection = intersection.getLine().getDirection();
            Vector reflectionDirection = getReflectionDirection(surfaceNormal, lineDirection);
            Line reflectionLine = new Line(intersection.getLocation(), reflectionDirection);
            reflectedIllumination = getColourOfLine(intersection.getShape(), reflectionLine, depthRemaining - 1)
                    .times(reflectivity);
        }
        return reflectedIllumination;
    }

    private static Vector getReflectionDirection(Vector surfaceNormal, Vector lineDirection) {
        // Shapes can return a surface normal pointing inwards or outwards. We want the one pointing in.
        if (surfaceNormal.dotProduct(lineDirection) > 0) {
            surfaceNormal = Vector.ZERO.minus(surfaceNormal);
        }
        return lineDirection.minus(surfaceNormal.mult(2*(lineDirection.dotProduct(surfaceNormal))));
    }

    private Colour getLightSourceIllumination(LineShapeIntersection intersection) {
        Colour illumination = scene.getAmbientLight();
        for (LightSource lightSource : scene.getLightSources()) {
            Vector distance = lightSource.getLocation().minus(intersection.getLocation());
            double alignment = distance.toUnit().dotProduct(intersection.getSurfaceNormal());
            if (alignment > 0) {
                double d = distance.scale();
                double distanceFactor = (d/lightSource.getBrightness());
                double effectiveBrightness = 1/(distanceFactor*distanceFactor); // Inverse square law
                if (effectiveBrightness > 1/256) { // If the light would be too dim, don't bother
                    Vector direction = distance.toUnit();
                    Line lineToLightSource = new Line(intersection.getLocation(), direction);

                    // Actually inverse shadowing
                    double shadowing = scene.getShapes().stream()
                            .flatMap(shape -> shape.intersections(lineToLightSource).stream())
                            .filter(i -> i.getShape() != intersection.getShape())
                            .filter(l -> l.getDistance() > 0 && l.getDistance() < d)
                            .mapToDouble(l -> 0d /* opacity */)
                            .reduce(1.0, (s, o) -> s * o);

                    if (shadowing > 0) {
                        Colour lightSourceIllumination = lightSource.getColour().times(alignment).times(shadowing);
                        lightSourceIllumination = lightSourceIllumination.times(effectiveBrightness);

                        lightSourceIllumination = applyFog(d, lightSourceIllumination);
                        illumination = illumination.add(lightSourceIllumination);
                    }
                }
            }
        }
        return illumination;
    }

    private Colour applyFog(double distance, Colour colour) {
        if (scene.getFogFactor() > 0) {
            double pow = distance/100 * scene.getFogFactor();
            double fogMultiplier = 1/Math.pow(Math.E, pow*pow);
            if (fogMultiplier > 1) {
                fogMultiplier = 1;
            }
            colour = colour.times(fogMultiplier).add(scene.getAmbientLight().times(1-fogMultiplier));
        }
        return colour;
    }
}
