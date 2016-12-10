package org.glencross.raytrace;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hacky class for loading the limited subset of Wavefront OBJ files needed for rendering the Utah teapot.
 */
public class WavefrontLoader {

    private WavefrontLoader() {
    }

    public static List<Shape> load(Reader reader, Vector offset, double scale, Surface surface) throws IOException {
        BufferedReader in = new BufferedReader(reader);

        List<Vector> vertices = new ArrayList<>();
        vertices.add(null); // 1 offset in the file!
        List<Shape> components = new ArrayList<>();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] words = line.split(" ");
            if (words.length < 1) {
                continue;
            }
            String recordType = words[0];
            switch (recordType) {
                case "o":
                    System.out.println("Object " + words[1]);
                    break;
                case "v":
                    assertMinTokens(words, 4);
                    vertices.add(new Vector(
                            Double.parseDouble(words[1])*scale,
                            Double.parseDouble(words[2])*scale,
                            Double.parseDouble(words[3])*scale)
                            .plus(offset));
                    break;
                case "f":
                    assertMinTokens(words, 4);
                    List<Vector> vectors = Arrays.asList(words).subList(1, words.length).stream()
                            .map(word -> word.split("/")[0])
                            .mapToInt(Integer::valueOf)
                            .mapToObj(vertices::get)
                            .collect(Collectors.toList());
                    if (vectors.size() == 3) {
                        components.add(new Triangle(vectors.get(0), vectors.get(1), vectors.get(2),
                                surface));
                    } else {
                        throw new UnsupportedOperationException("Face with " + vectors.size() +
                                " vertices not supported. We only do triangles at the moment.");
                    }
                    break;
            }

        }

        return components;
    }

    private static void assertMinTokens(String[] words, int minLength) throws IOException {
        if (words.length < minLength) {
            throw new IOException("Not enough tokens in line");
        }
    }

    public static void main(String[] args) throws IOException {
        List<Shape> shape = WavefrontLoader.load(new FileReader("teapot.obj"), new Vector(0,0,0), 500, new Surface(Colour.GREY, 0.9));
        System.out.println(shape);
    }

}
