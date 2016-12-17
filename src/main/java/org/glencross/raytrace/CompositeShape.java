package org.glencross.raytrace;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Recursive data structure which subdivides the volume of its bounding box into 8 equally sized sub-boxes, and assigns
 * shapes to those boxes. This reduces the number of intersection checks that needs to be performed when a ray hits
 * the composite shape.
 */
public class CompositeShape implements Shape {

    private final BoundingBox boundingBox;
    private final List<Shape> topLevelComponents = new ArrayList<>();
    private final List<CompositeShape> children = new ArrayList<>();

    public CompositeShape(List<Shape> shapes) {

        this.boundingBox = shapes.stream()
                .map(Shape::getBoundingBox)
                .reduce(BoundingBox.NONE, BoundingBox::combine);

        Map<Boolean, List<Shape>> shapesByHasBoundingBox = shapes.stream()
                .collect(Collectors.partitioningBy(s -> !s.getBoundingBox().equals(BoundingBox.INFINITE)));
        topLevelComponents.addAll(shapesByHasBoundingBox.getOrDefault(Boolean.FALSE, Collections.emptyList()));
        shapes = shapesByHasBoundingBox.getOrDefault(Boolean.TRUE, Collections.emptyList());

        if (shapes.size() < 5) {
            topLevelComponents.addAll(shapes);
        } else {
            partitionShapes(shapes);
        }

    }

    /**
     * Divides this component into 8 equally sized bounding boxes and assigns the shapes to those boxes.
     * If they contain sufficient components, those boxes may also be divided up recursively.
     */
    private void partitionShapes(List<Shape> shapes) {

        BoundingBox boundingBox = shapes.stream()
                .map(Shape::getBoundingBox)
                .reduce(BoundingBox.NONE, BoundingBox::combine);

        Vector min = boundingBox.getMin();
        Vector max = boundingBox.getMax();
        Vector mid = boundingBox.getMin().plus(boundingBox.getMax()).mult(0.5);
        List<BoundingBox> childBoxes = new ArrayList<>();
        childBoxes.addAll(Arrays.asList(
                new BoundingBox(
                        new Vector(min.getX(), min.getY(), min.getZ()),
                        new Vector(mid.getX(), mid.getY(), mid.getZ())),
                new BoundingBox(
                        new Vector(mid.getX(), min.getY(), min.getZ()),
                        new Vector(max.getX(), mid.getY(), mid.getZ())),
                new BoundingBox(
                        new Vector(min.getX(), mid.getY(), min.getZ()),
                        new Vector(mid.getX(), max.getY(), mid.getZ())),
                new BoundingBox(
                        new Vector(min.getX(), min.getY(), mid.getZ()),
                        new Vector(mid.getX(), mid.getY(), max.getZ())),
                new BoundingBox(
                        new Vector(mid.getX(), mid.getY(), min.getZ()),
                        new Vector(max.getX(), max.getY(), mid.getZ())),
                new BoundingBox(
                        new Vector(mid.getX(), min.getY(), mid.getZ()),
                        new Vector(max.getX(), mid.getY(), max.getZ())),
                new BoundingBox(
                        new Vector(min.getX(), mid.getY(), mid.getZ()),
                        new Vector(mid.getX(), max.getY(), max.getZ())),
                new BoundingBox(
                        new Vector(mid.getX(), mid.getY(), mid.getZ()),
                        new Vector(max.getX(), max.getY(), max.getZ()))
                ));

        // Put shapes into boxes
        Map<BoundingBox, List<Shape>> shapesByBox = new HashMap<>();
        childBoxes.stream().forEach(box -> {
            List<Shape> boxShapes = shapes.stream()
                    .filter(s -> box.intersects(s.getBoundingBox()))
                    .collect(Collectors.toList());
            if (boxShapes.size() > 1 && boxShapes.size() < shapes.size() - 1) {
                shapesByBox.put(box, boxShapes);
            } else {
                topLevelComponents.addAll(boxShapes);
            }
        });

        // Merge any identical boxes
        Map<List<Shape>, List<BoundingBox>> identicalBoxes = shapesByBox.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getValue(),
                        Collectors.mapping(e -> e.getKey(),
                                Collectors.toList())));
        identicalBoxes.forEach((s, boxes) -> {
            if (boxes.size() > 1) {
                boxes.forEach(shapesByBox::remove);
                BoundingBox combined = boxes.stream().reduce(BoundingBox::combine).get();
                shapesByBox.put(combined, s);
            }
        });

        // Remove empty boxes
        for (Iterator<Map.Entry<BoundingBox, List<Shape>>> i = shapesByBox.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<BoundingBox, List<Shape>> e = i.next();
            if (e.getValue().isEmpty()) {
                i.remove();
            }
        }

        // As long as there is at least one child box which contains less than half the items, recurse
        if (shapesByBox.values().stream().anyMatch(s -> s.size() < shapes.size()/2)) {
            shapesByBox.entrySet().stream()
                    .map(e -> new CompositeShape(e.getValue()))
                    .forEach(this.children::add);
        } else {
            shapesByBox.values().stream()
                    .forEach(this.topLevelComponents::addAll);
        }
    }

    @Override
    public List<LineShapeIntersection> intersections(Line line) {
        Set<Shape> shapes = new HashSet<>();
        collectShapesByBoundingBox(line, shapes);
        return shapes.stream()
                .flatMap(s -> s.intersections(line).stream())
                .map(i -> new LineShapeIntersection(line, this,  i.getDistance(), i.getLocation(),
                        i.getSurfaceNormal(), i.getSurfaceProperties()))
                .collect(Collectors.toList());
    }

    private void collectShapesByBoundingBox(Line line, Set<Shape> shapes) {
        if (!boundingBox.intersects(line)) {
            return;
        }
        shapes.addAll(topLevelComponents);
        children.forEach(child -> child.collectShapesByBoundingBox(line, shapes));
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return "CompositeShape:\n" + toString("  ");
    }

    private String toString(String indent) {
        return  indent + "top=" + topLevelComponents + "\n" +
                indent + "nested=\n  " + indent + children.stream()
                .map(e -> e.toString(indent + "  "))
                .collect(Collectors.joining("\n"));
    }
}
