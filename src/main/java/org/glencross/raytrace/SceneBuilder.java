package org.glencross.raytrace;

import java.util.List;

public class SceneBuilder {
    private Vector viewerLocation;
    private Vector viewerDirection;
    private double distanceFromScreen;
    private Colour ambientLight;
    private List<LightSource> lightSources;
    private List<Shape> shapes;
    private double fogFactor;

    public SceneBuilder setViewerLocation(Vector viewerLocation) {
        this.viewerLocation = viewerLocation;
        return this;
    }

    public SceneBuilder setViewerDirection(Vector viewerDirection) {
        this.viewerDirection = viewerDirection;
        return this;
    }

    public SceneBuilder setDistanceFromScreen(double distanceFromScreen) {
        this.distanceFromScreen = distanceFromScreen;
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
        return new Scene(viewerLocation, viewerDirection, distanceFromScreen, ambientLight, lightSources, shapes, fogFactor);
    }
}