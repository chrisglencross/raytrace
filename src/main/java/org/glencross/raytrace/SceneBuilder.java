package org.glencross.raytrace;

import java.util.Collections;
import java.util.List;

public class SceneBuilder {
    private Vector viewerLocation = new Vector(0, 1, -1);
    private Vector viewerDirection = new Vector(0, 0, 1);
    private Vector topOfScreenDirection = new Vector(0, 1, 0);
    private double horizontalFieldOfViewDegrees = 60d;
    private Colour ambientLight = Colour.BLACK;
    private List<LightSource> lightSources = Collections.emptyList();
    private List<Shape> shapes = Collections.emptyList();
    private double fogFactor = 0.05d;

    public SceneBuilder setViewerLocation(Vector viewerLocation) {
        this.viewerLocation = viewerLocation;
        return this;
    }

    public SceneBuilder setViewerDirection(Vector viewerDirection) {
        this.viewerDirection = viewerDirection;
        return this;
    }

    public SceneBuilder setTopOfScreenDirection(Vector topOfScreenDirection) {
        this.topOfScreenDirection = topOfScreenDirection;
        return this;
    }

    public SceneBuilder setHorizontalFieldOfViewDegrees(double horizontalFieldOfViewDegrees) {
        this.horizontalFieldOfViewDegrees = horizontalFieldOfViewDegrees;
        return this;
    }

    public SceneBuilder setAmbientLight(Colour ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public SceneBuilder setLightSources(List<LightSource> lightSources) {
        this.lightSources = lightSources;
        return this;
    }

    public SceneBuilder setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        return this;
    }

    public SceneBuilder setFogFactor(double fogFactor) {
        this.fogFactor = fogFactor;
        return this;
    }

    public Scene createScene() {
        return new Scene(viewerLocation, viewerDirection, topOfScreenDirection, horizontalFieldOfViewDegrees,
                ambientLight, lightSources, shapes, fogFactor);
    }
}