package org.glencross.raytrace;

import java.util.List;
import java.util.stream.Collectors;

public class CompositeShape implements Shape {

    private final List<Shape> components;

    public CompositeShape(List<Shape> components) {
        this.components = components;
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        // TODO - would be nice to use some bounding box algorithm here!
        return components.stream()
                .flatMap(c -> c.intersections(line).stream())
                .map(i -> new LineShapeIntersection(line, this,  i.getDistance(), i.getLocation(),
                        i.getSurfaceNormal(), i.getSurfaceProperties()))
                .collect(Collectors.toList());
    }

}
