package view;

import rasterize.ImageBuffer;
import transforms.Col;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private final ImageBuffer raster;

    private static final int WIDTH = 800, HEIGHT = 600;

    Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new ImageBuffer(WIDTH, HEIGHT);
        raster.setClearValue(new Col(Color.RED.getRGB()));
        raster.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
    }

    public ImageBuffer getImageBuffer() {
        return raster;
    }

}
