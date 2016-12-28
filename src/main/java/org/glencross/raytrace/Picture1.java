package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * My first picture.
 */
public class Picture1 {

    public static void main(String[] args) throws IOException {

        SceneBuilder builder = new SceneBuilder();
        builder.setViewerLocation(new Vector(0, 100, -800));
        builder.setViewerDirection(new Vector(0, -0.1, 1).toUnit());
        builder.setAmbientLight(new Colour(0.5, 0.5, 0.5));
        builder.setLightSources(Arrays.asList(
                new LightSource( Colour.WHITE, new Vector(300, 300, -300), 300)));


        builder.setTopOfScreenDirection(new Vector(0.2, 1, 0).toUnit());
        CompositeShape pyramid = new CompositeShape(Arrays.asList(
                new Triangle(new Vector(-600, -200, 450), new Vector(0, -200, 450), new Vector(-200, 200, 350),
                        new Surface(Colour.RED, 0.6)),
                new Triangle(new Vector(-600, -200, 450), new Vector(-300, -200, 250), new Vector(-200, 200, 350),
                        new Surface(Colour.ORANGE, 0.6)),
                new Triangle(new Vector(0, -200, 450), new Vector(-300, -200, 250), new Vector(-200, 200, 350),
                        new Surface(Colour.YELLOW, 0.6))
                ));

            builder.setShapes(Arrays.asList(
//                new Sphere(new Vector(-280, -50, 50), 150, new Surface(new Colour(1, 0.4, 0.4),
//                        0.2)),
                new Sphere(new Vector(100, 0, 140), 200, new Surface(Colour.GREY, 0.6)),
                new Sphere(new Vector(-60, -140, -150), 60, new Surface(Colour.GREEN, 0.8)),
                pyramid,
                new ChequeredPlane(new Vector(0, 1, 0).toUnit(), -200,
                        new Surface(Colour.BLUE, 0.7),
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

        RayTracer rayTracer = new RayTracer(scene, 800, 600);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "JPEG", new File("picture1.jpg"));

    }


}
