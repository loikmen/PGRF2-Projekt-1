package rasterize;

import transforms.Col;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImageBuffer implements Raster<Col> {

    private final BufferedImage img;
    private Col clearColor;

    public ImageBuffer(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void repaint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public Graphics getGraphics() {
        return img.getGraphics();
    }

    @Override
    public Optional<Col> getElement(int x, int y) {
        return Optional.empty(); // TODO
    }

    @Override
    public void setElement(int x, int y, Col value) {
        // TODO
    }

    @Override
    public void clear() {
        // různé zavolání getGraphics() vrací různé reference
        Graphics g = getGraphics();
        g.setColor(new Color(clearColor.getRGB()));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    @Override
    public void setClearValue(Col value) {
        this.clearColor = value;
    }

    @Override
    public int getWidth() {
        return 0; // TODO
    }

    @Override
    public int getHeight() {
        return 0; // TODO
    }

}
