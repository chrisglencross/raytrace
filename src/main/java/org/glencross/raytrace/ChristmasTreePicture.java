package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChristmasTreePicture {

    public static void main(String[] args) throws IOException {

        List<LightSource> lightSources = new ArrayList<>();
        lightSources.add(new LightSource(Colour.WHITE, new Vector(-2, 0.1, -2), 1.5));
        lightSources.add(new LightSource(Colour.WHITE, new Vector( 2, 0.1, -2), 1.5));

        List<Shape> shapes = new ArrayList<>();
        shapes.addAll(Arrays.asList(
                // Tree
                new Cone(
                        new Vector(0, 3.5 ,0),
                        new Vector(0, -1, 0).toUnit(),
                        1.5,
                        2.5,
                        new Surface(Colour.DARK_GREEN, 0.3)),
                // Pot
                new Cone(
                        new Vector(0, -3 ,0),
                        new Vector(0, 1, 0).toUnit(),
                        0.7,
                        3.5,
                        new Surface(Colour.BLUE, 1)),
                // Trunk
                new Cylinder(
                        new Vector(0, 0 ,0),
                        new Vector(0, 1, 0).toUnit(),
                        0.2,
                        1,
                        new Surface(Colour.BROWN, 0.2)),
                new ChequeredPlane(new Vector(0, 1, 0).toUnit(), 0,
                        new Surface(Colour.RED, 0.5),
                        new Surface(Colour.GREEN, 0.9),
                        0.2
                )));

        // Baubles and candles
        for (int i = 0; i < 200; i++) { // 200
            double h = (i*1.4582d) % 1d;
            h = (1-(h*h))*2.4;
            double r = 0.02+(h/2.5)*1.5;
            double a = (i * 43d) % 360;
            double x = r * Math.cos(Math.toRadians(a));
            double y = r * Math.sin(Math.toRadians(a));
            Vector location = new Vector(x, (2.5-h)+1, y);
            switch (i % 4) {
                case 0:
                    shapes.add(new Sphere(location, 0.05, new Surface(Colour.RED, 1)));
                    break;
                case 1:
                    shapes.add(new Sphere(location, 0.05, new Surface(Colour.GREEN, 1)));
                    break;
                case 2:
                case 3:
                    shapes.add(new Sphere(
                            new Vector(location.getX(), location.getY()+0.06, location.getZ()),
                            0.02, new Surface(Colour.LIGHT_YELLOW, 1)));
                    shapes.add(new Cylinder(
                            new Vector(location.getX(), location.getY()-0.04, location.getZ()),
                            new Vector(0, 1, 0), 0.02, 0.08, new Surface(Colour.WHITE, 1))
                    );
                    lightSources.add(new LightSource(Colour.LIGHT_YELLOW,
                            new Vector(location.getX(), location.getY()+0.085, location.getZ()), 0.1));
                    break;
            }
        }

        // Tinsel
        for (int i = 0; i < 360; i++) {
            double h = i/360d;
            h = (1-h)*2.5;
            double r = 0.05+(h/2.5)*1.5;
            double a = (i * 3d) % 360;
            double x = r * Math.cos(Math.toRadians(a));
            double y = r * Math.sin(Math.toRadians(a));
            Vector location = new Vector(x, (2.5-h)+1, y);
            shapes.add(new Sphere(location, 0.05, new Surface(Colour.WHITE, 1)));
        }

        // Star
        shapes.add(new Triangle(new Vector(0, 3.4, -0.2), new Vector(-0.15, 3.7, -0.2), new Vector(0.15, 3.7, -0.2),
                new Surface(Colour.LIGHT_YELLOW, 1)));
        shapes.add(new Triangle(new Vector(-0.15, 3.5, -0.2), new Vector(0, 3.8, -0.2), new Vector(0.15, 3.5, -0.2),
                new Surface(Colour.LIGHT_YELLOW, 1)));

        SceneBuilder builder = new SceneBuilder();
        builder.setViewerLocation(new Vector(0, 1.5, -4));
        builder.setDistanceFromScreen(2);
        builder.setViewerDirection(new Vector(0, -0.1, 1).toUnit());
        builder.setAmbientLight(Colour.BLACK);
        builder.setLightSources(lightSources);
        builder.setShapes(Collections.singletonList(new CompositeShape(shapes)));
        builder.setFogFactor(0.01);
        Scene scene = builder.createScene();

        RayTracer rayTracer = new RayTracer(scene, 1080, 1440);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "PNG", new File("christmas.png"));

    }


}
