package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TeapotPicture {

    public static void main(String[] args) throws IOException {

        SceneBuilder builder = new SceneBuilder();
        builder.setViewerLocation(new Vector(0, 300, -800));
        builder.setViewerDirection(new Vector(0, -0.1, 1).toUnit());
        builder.setAmbientLight(new Colour(0.1, 0.1, 0.1));
        builder.setLightSources(Arrays.asList(
                new LightSource( Colour.WHITE, new Vector(300, 300, -300), 300),
                new LightSource( Colour.GREY, new Vector(-300, 300, -300), 300)
                ));

        List<Shape> teapotComponents = WavefrontLoader.load(
                new FileReader("teapot.obj"),
                new Vector(0,0,0),
                400,
                new Surface(Colour.GREY, 0.9));
        Shape teapot = new CompositeShape(teapotComponents);

        builder.setShapes(Arrays.asList(
                teapot,
                new ChequeredPlane(new Vector(0, 1, 0).toUnit(), 0,
                        new Surface(Colour.YELLOW, 0.7),
                        new Surface(Colour.CYAN, 0.2),
                        150
                )
        ));
        builder.setFogFactor(0.05);
        Scene scene = builder.createScene();

        RayTracer rayTracer = new RayTracer(scene, 1600, 1200);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "JPEG", new File("teapot.jpg"));

    }


}
