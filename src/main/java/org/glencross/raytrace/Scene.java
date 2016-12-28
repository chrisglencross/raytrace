package org.glencross.raytrace;

import java.util.List;

public class Scene {

    private final Vector viewerLocation;
    private final Vector viewerDirection;
    private final Vector topOfScreenDirection;
    private final double horizontalFieldOfViewDegrees;

    private final Colour ambientLight;
    private final List<LightSource> lightSources;
    private final List<Shape> shapes;
    private final double fogFactor;

    public Scene(Vector viewerLocation, Vector viewerDirection, Vector topOfScreenDirection,
                 double horizontalFieldOfViewDegrees, Colour ambientLight, List<LightSource> lightSources, List<Shape> shapes,
                 double fogFactor) {
        this.viewerLocation = viewerLocation;
        this.viewerDirection = viewerDirection;
        this.topOfScreenDirection = topOfScreenDirection;
        this.horizontalFieldOfViewDegrees = horizontalFieldOfViewDegrees;
        this.ambientLight = ambientLight;
        this.lightSources = lightSources;
        this.shapes = shapes;
        this.fogFactor = fogFactor;
    }

    public Vector getViewerLocation() {
        return viewerLocation;
    }

    public Vector getViewerDirection() {
        return viewerDirection;
    }

    public Vector getTopOfScreenDirection() {
        return topOfScreenDirection;
    }

    public double getHorizontalFieldOfViewDegrees() {
        return horizontalFieldOfViewDegrees;
    }

    public Colour getAmbientLight() {
        return ambientLight;
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public double getFogFactor() {
        return fogFactor;
    }
}
