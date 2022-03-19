package controller;

import javafx.scene.Scene;
import model.Part;
import model.TopologyType;
import model.Vertex;
import rasterize.Raster;
import renderer.GPURenderer;
import renderer.Renderer3D;
import shader.DepthShader;
import shader.Shader;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Controller3D {

    private final Panel panel;
    private final GPURenderer renderer;

    private List<Part> partBuffer;
    private List<Integer> indexBuffer;
    private List<Vertex> vertexBuffer;

    private Mat4 projection;
    private Camera camera;
    public int mode = 0;
    private Scene mainScene, axisScene;
    private Raster raster;
    public Mat4 model;
    public int bodX, bodY, noveX, noveY;
    private Mat4 projectionOrtho;

    public Controller3D(Panel panel) {
        this.panel = panel;
        renderer = new Renderer3D(panel.getImageBuffer());
        initListeners();
        this.raster = panel.getImageBuffer();
        model = new Mat4Identity();

        camera = new Camera()
                .withPosition(new Vec3D(0.8, -5, 1))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-15));

        //zvetseni
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_O) {


                    Mat4 velikost = new Mat4Scale(1.2, 1.2, 1.2);
                    model = model.mul(velikost);

                 //   System.out.println(model);
                    renderer.setModel(model);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);



                }


            }

        });

        //zmenseni
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_L) {


                    Mat4 velikost = new Mat4Scale(0.8, 0.8, 0.8);
                    model = model.mul(velikost);


                    renderer.setModel(model);



                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);


                }

            }

        });


//pohyb doleva
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {
                if (c.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(0.5);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);
                     //   System.out.println(camera.getPosition());

                }


            }

        });


//pohyb doleva
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {
                if (c.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(0.5);
                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);
                     //   System.out.println(camera.getPosition());

                }


            }

        });

        //pohyb dopředu
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_W) {

                    camera = camera.forward(0.5);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);



                }


            }

        });

        //pohyb dozadu
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(0.5);
                    System.out.println();


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);



                }


            }

        });

        //pohyb objektu doprava
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_RIGHT) {


                    Mat4 pozice = new Mat4Transl(1, 0, 0);
                    model = model.mul(pozice);


                    renderer.setModel(model);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);


                }


            }

        });

        //pohyb objektu doleva
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_LEFT) {


                    Mat4 pozice = new Mat4Transl(-1, 0, 0);
                    model = model.mul(pozice);


                    renderer.setModel(model);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);


                }


            }

        });

        //pohyb objektu nahoru
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_UP) {


                    Mat4 pozice = new Mat4Transl(0, 0, 1);
                    model = model.mul(pozice);


                    renderer.setModel(model);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);


                }


            }

        });

        //pohyb objektu dolu
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent c) {


                if (c.getKeyCode() == KeyEvent.VK_DOWN) {


                    Mat4 pozice = new Mat4Transl(0, 0, -1);
                    model = model.mul(pozice);


                    renderer.setModel(model);


                        raster.clear();
                        renderer.setView(camera.getViewMatrix());
                        renderer.setProjection(projection);
                        renderer.draw(partBuffer,indexBuffer,vertexBuffer);


                }


            }

        });

        //rozhlížení
        MouseAdapter mousePressed = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                bodX = e.getX();
                bodY = e.getY();
            }
        };

        panel.addMouseListener(mousePressed);


        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                noveX = bodX;
                noveY = bodY;

                bodX = e.getX();
                bodY = e.getY();

                int moveX = bodX - noveX;
                int moveY = bodY - noveY;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    camera = camera.addAzimuth((double) -moveX * Math.PI / 720);
                    camera = camera.addZenith((double) -moveY * Math.PI / 720);
                    System.out.println(camera);
                }else if(SwingUtilities.isRightMouseButton(e)){
                    Mat4RotXYZ otoceni = new Mat4RotXYZ(0, moveY * Math.PI / 180, moveX * Math.PI / 180);
                    model = model.mul(otoceni);
                    renderer.setModel(model);
                }



                    raster.clear();
                    renderer.setView(camera.getViewMatrix());
                    renderer.setProjection(projection);
                    renderer.draw(partBuffer,indexBuffer,vertexBuffer);



            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

//přiblížení a oddálení
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    camera = camera.forward(0.5);
                    System.out.println(camera);
                } else {
                    camera = camera.backward(0.5);
                    System.out.println(camera);
                }
                raster.clear();



                    raster.clear();
                    renderer.setView(camera.getViewMatrix());
                    renderer.setProjection(projection);
                    renderer.draw(partBuffer,indexBuffer,vertexBuffer);




            }
        });

        partBuffer = List.of(new Part(TopologyType.TRIANGLE, 0, 1));
        indexBuffer = List.of(0, 1, 2);
        vertexBuffer = List.of(
                new Vertex(new Point3D(0.2, 0.2, 0.2), new Col(Color.WHITE.getRGB())),
                new Vertex(new Point3D(0.6, 0.5, 0.5), new Col(Color.YELLOW.getRGB())),
                new Vertex(new Point3D(0.7, 0.3, 0.7), new Col(Color.WHITE.getRGB()))
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



        projection = new Mat4PerspRH(
                Math.PI / 3,
                raster.getHeight() / (float) raster.getWidth(),
                0.1,
                50.0
        );
        projectionOrtho = new Mat4OrthoRH(20, 20, 0.1, 50.0);

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
