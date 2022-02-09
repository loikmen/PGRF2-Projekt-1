package rasterize;

import java.util.Optional;

public class DepthBuffer implements Raster<Double> {

    private final double[][] zBuffer;

    public DepthBuffer(int width, int height) {
        zBuffer = new double[width][height];
    }

    @Override
    public Optional<Double> getElement(int x, int y) {
        return Optional.empty();
    }

    @Override
    public void setElement(int x, int y, Double value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void setClearValue(Double value) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
