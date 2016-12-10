package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by chris on 04/12/16.
 */
public class Picture1 {

    public static void main(String[] args) throws IOException {

        SceneBuilder builder = new SceneBuilder();
        builder.setViewerLocation(new Vector(0, 100, -800));
        builder.setDistanceFromScreen(300);
        builder.setViewerDirection(new Vector(0, -0.1, 1).toUnit());
        builder.setAmbientLight(new Colour(0.5, 0.5, 0.5));
        builder.setLightSources(Arrays.asList(
                new LightSource( Colour.WHITE, new Vector(300, 300, -300), new Vector(-1, -1, 1).toUnit())));
        builder.setShapes(Arrays.asList(
                new Sphere(new Vector(-280, -50, 50), 150, new Surface(new Colour(1, 0.4, 0.4),
                        0.2)),
                new Sphere(new Vector(100, 0, 140), 200, new Surface(Colour.GREY, 0.6)),
                new Sphere(new Vector(-60, -140, -150), 60, new Surface(Colour.GREEN, 0.8)),
                new Triangle(new Vector(-400, 0, 300), new Vector(0, 0, 450), new Vector(-200, 400, 400),
                        new Surface(Colour.RED, 0.6)),
                new ChequeredPlane(new Vector(0, 1, 0).toUnit(), -200,
                        new Surface(Colour.YELLOW, 0.7),
                        new Surface(Colour.CYAN, 0.2),
                        150
                ),
                new ChequeredPlane(new Vector(0, 0, -1).toUnit(), -2000,
                        new Surface(Colour.RED, 0.2),
                        new Surface(Colour.GREEN, 0),
                        150
                )
        ));
        builder.setFogFactor(0.05);
        Scene scene = builder.createScene();

        RayTracer rayTracer = new RayTracer(scene, 1600, 1200);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "JPEG", new File("output.jpg"));

    }


}
