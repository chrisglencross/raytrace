package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CylinderPicture {

    public static void main(String[] args) throws IOException {

        SceneBuilder builder = new SceneBuilder();
        builder.setViewerLocation(new Vector(0, 2, -4));
        builder.setDistanceFromScreen(2);
        builder.setViewerDirection(new Vector(0, -0.3, 1).toUnit());
        builder.setAmbientLight(new Colour(0.1, 0.1, 0.1));
        builder.setLightSources(Arrays.asList(
                new LightSource( Colour.WHITE, new Vector(300, 300, -300), new Vector(-1, -1, 1).toUnit()),
                new LightSource( Colour.WHITE, new Vector(-300, 300, -300), new Vector(1, -1, 1).toUnit())
                ));
        builder.setShapes(Arrays.asList(
                new Cylinder(
                        new Vector(0, 0 ,0),
                        new Vector(0, 1, 0).toUnit(),
                        1,
                        5,
                        new Surface(Colour.WHITE, 0.7)),
                new Sphere(
                        new Vector(-1.5, 1, -1.5),
                        1,
                        new Surface(Colour.GREEN, 0)
                ),
                new ChequeredPlane(new Vector(0, 1, 0).toUnit(), 0,
                        new Surface(Colour.BLUE, 0.7),
                        new Surface(Colour.CYAN, 0.2),
                        0.1
                )
        ));
        builder.setFogFactor(0.05);
        Scene scene = builder.createScene();

        RayTracer rayTracer = new RayTracer(scene, 400, 300);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "JPEG", new File("cylinder.jpg"));

    }


}
