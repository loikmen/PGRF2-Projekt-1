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

        return Optional.of(new Col(img.getRGB(x,y)));


        // TODO

       // return img.getRGB(x,y);

    }

    @Override
    public void setElement(int x, int y, Col value) {
        img.setRGB(x, y, value.getRGB());
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
       //return 0;
        // TODO
        return img.getWidth();
    }

    @Override
    public int getHeight() {
       // return 0;
        // TODO
        return img.getHeight();
    }

}
