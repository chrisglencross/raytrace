package org.glencross.raytrace;

import java.util.List;

public interface Shape {

    List<LineShapeIntersection> intersections(Line line);

}
