package org.glencross.raytrace;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.math.BigDecimal;
import java.util.Comparator;
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

        double horizontalFieldOfViewDegrees = 60; // Could make this a property of the scene?
        double verticalFieldOfViewDegrees = horizontalFieldOfViewDegrees / width * height;

        Vector screenCentreDirection = scene.getViewerDirection();
        double topAngle = Math.toRadians(verticalFieldOfViewDegrees/2);
        double leftAngle = Math.toRadians(-horizontalFieldOfViewDegrees/2);
        double pixelAngle = Math.toRadians(verticalFieldOfViewDegrees/height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        IntStream.range(0, height).forEach(y -> {
            Vector rowDirection = Matrix.rotateAroundXAxis(topAngle-y*pixelAngle).multiply(screenCentreDirection);
            IntStream.range(0, width).parallel().forEach(x -> {
                Vector pixelDirection = Matrix.rotateAroundYAxis(leftAngle+x*pixelAngle).multiply(rowDirection).toUnit();
                Line line = new Line(scene.getViewerLocation(), pixelDirection);
                Colour colour = getColourOfLine(null, line, 5);
                int intColour = colour.toInt();
                image.setRGB(x, y, intColour);
            });
            System.out.println(new BigDecimal(100d*y/height).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
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
                Vector direction = distance.toUnit();
                Line lineToLightSource = new Line(intersection.getLocation(), direction);

                // Actually inverse shadowing
                double shadowing = scene.getShapes().stream()
                        .flatMap(shape -> shape.intersections(lineToLightSource).stream())
                        .filter(i -> i.getShape() != intersection.getShape())
                        .filter(l -> l.getDistance() > 0 && l.getDistance() < d)
                        .mapToDouble(l -> 0d /* opacity */)
                        .reduce(1.0, (s, o) -> s*o);

                if (shadowing > 0) {
                    Colour lightSourceIllumination = lightSource.getColour().times(alignment).times(shadowing);
                    // Inverse square law
                    double distanceFactor = (distance.scale()/lightSource.getBrightness());
                    lightSourceIllumination = lightSourceIllumination.times(1/(distanceFactor*distanceFactor));

                    lightSourceIllumination = applyFog(d, lightSourceIllumination);
                    illumination = illumination.add(lightSourceIllumination);
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
