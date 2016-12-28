package org.glencross.raytrace;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ChristmasTreePicture2 {

    public static void main(String[] args) throws IOException {

        // Same as ChristmasTreePicture but facing the other way
        SceneBuilder builder = ChristmasTreePicture.getSceneBuilder();
        builder.setViewerLocation(new Vector(0, 1.5, -4));
        builder.setViewerDirection(new Vector(0, 0.1, -1).toUnit());
        Scene scene = builder.createScene();

        RayTracer rayTracer = new RayTracer(scene, 200, 200);
        RenderedImage image = rayTracer.render();
        ImageIO.write(image, "PNG", new File("christmas2.png"));
    }


}
