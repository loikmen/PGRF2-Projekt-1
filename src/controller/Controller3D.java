package controller;

import model.Part;
import model.TopologyType;
import model.Vertex;
import renderer.GPURenderer;
import renderer.Renderer3D;
import shader.DepthShader;
import shader.Shader;
import transforms.Camera;
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

        Shader<Vertex, Col> shader = Vertex::getColor;
        renderer.setShader(shader);

        Shader<Vertex, Col> shaderCyan = vertex -> new Col(Color.CYAN.getRGB());
        renderer.setShader(shaderCyan);

        Shader<Vertex, Col> shaderNoGreen = vertex -> {
            return new Col(vertex.getColor().getR(), 0.0, vertex.getColor().getB());
        };
        renderer.setShader(shaderNoGreen);

        Shader<Vertex, Col> shaderX2 = vertex -> {
            return Math.round(vertex.getPoint().getX()) % 2 == 0
                    ? new Col(255, 255, 0) : new Col(0, 0, 255);
        };
        renderer.setShader(shaderX2);

        Shader<Vertex, Col> shaderY2 = vertex -> {
            return Math.round(vertex.getPoint().getY()) % 4 == 0
                    ? new Col(255, 0, 0) : vertex.getColor();
        };
        renderer.setShader(shaderY2);

        Shader<Vertex, Col> shaderZ = vertex -> {
            double z = 1 - vertex.getZ();
            return new Col(z, z, z);
        };
        renderer.setShader(shaderZ);

        var shaderZ2 = new DepthShader();
        renderer.setShader(shaderZ2);


        display();

        Camera camera = new Camera();
        camera = camera.right(-5);
        System.out.println(camera.getPosition());
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
