package controller;

import model.Part;
import model.TopologyType;
import model.Vertex;
import renderer.GPURenderer;
import renderer.Renderer3D;
import transforms.Col;
import transforms.Point3D;
import view.Panel;

import java.awt.*;
import java.util.List;

public class Controller3D {

    private final Panel panel;
    private final GPURenderer renderer;

    private List<Part> partBuffer;
    private List<Integer> indexBuffer;
    private List<Vertex> vertexBuffer;

    public Controller3D(Panel panel) {
        this.panel = panel;
        renderer = new Renderer3D(panel.getImageBuffer());
        initListeners();

        partBuffer = List.of(new Part(TopologyType.TRIANGLE, 0, 1));
        indexBuffer = List.of(0, 1, 2);
        vertexBuffer = List.of(
                new Vertex(new Point3D(0.2, 0.2, 0.2), new Col(Color.WHITE.getRGB())),
                new Vertex(new Point3D(0.6, 0.5, 0.5), new Col(Color.YELLOW.getRGB())),
                new Vertex(new Point3D(0.7, 0.3, 0.7), new Col(Color.GREEN.getRGB()))
        );
        display();
    }

    private void display() {
        renderer.clear();

        renderer.draw(partBuffer, indexBuffer, vertexBuffer);

        panel.repaint();
    }

    private void initListeners() {
//        panel.addMouseListener();
    }

}
