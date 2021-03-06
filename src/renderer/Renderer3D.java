package renderer;

import model.Part;
import model.TopologyType;
import model.Vertex;
import rasterize.DepthBuffer;
import rasterize.ImageBuffer;
import shader.Shader;
import transforms.*;

import java.util.List;
import java.util.Optional;

public class Renderer3D implements GPURenderer {

    private final ImageBuffer imageBuffer;
    private final DepthBuffer depthBuffer;

    private Mat4 model = new Mat4Identity();
    private Mat4 view = new Mat4Identity();
    private Mat4 projection = new Mat4Identity();
    private Shader<Vertex, Col> shader;

    public Renderer3D(ImageBuffer imageBuffer) {
        this.imageBuffer = imageBuffer;
        depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());

//        shader = new Shader<Vertex, Col>() {
//            @Override
//            public Col shade(Vertex vertex) {
//                return vertex.getColor();
//            }
//        };
//        shader = vertex -> {
//            return vertex.getColor();
//        };
//        shader = vertex -> vertex.getColor();
        shader = Vertex::getColor;
    }

    @Override
    public void draw(List<Part> partsBuffer, List<Integer> indexBuffer, List<Vertex> vertexBuffer) {
        for (Part part : partsBuffer) {
            TopologyType topologyType = part.getTopologyType();
            int start = part.getStart();
            int count = part.getCount();
            if (topologyType == TopologyType.TRIANGLE) {
                for (int i = start; i < start + count * 3; i += 3) {
                    Integer index1 = indexBuffer.get(i);
                    Integer index2 = indexBuffer.get(i + 1);
                    Integer index3 = indexBuffer.get(i + 2);
                    Vertex vertex1 = vertexBuffer.get(index1);
                    Vertex vertex2 = vertexBuffer.get(index2);
                    Vertex vertex3 = vertexBuffer.get(index3);
                    prepareTriangle(vertex1, vertex2, vertex3);
                }
            }
//            else if (topologyType == TopologyType.LINE) {
//
//            } ...
        }
    }

    private void prepareTriangle(Vertex v1, Vertex v2, Vertex v3) {
        // 1. transformace vrchol??
        Vertex a = new Vertex(
                v1.getPoint().mul(model).mul(view).mul(projection),
                v1.getColor()
        );
        // TODO transformovat ostatn?? vrcholy
        Vertex b = new Vertex(
                v2.getPoint().mul(model).mul(view).mul(projection),
                v2.getColor()
        );
        Vertex c = new Vertex(
                v3.getPoint().mul(model).mul(view).mul(projection),
                v3.getColor()
        );

        // 2. o??ez??n??
        // o??ezat ty troj??heln??ky, kter?? jsou cel?? mimo zobrazovac?? objem
        if (a.getX() > a.getW() && b.getX() > b.getW() && c.getX() > c.getW()) return;
        if (a.getY() > a.getW() && b.getY() > b.getW() && c.getY() > c.getW()) return;
        if (a.getZ() > a.getW() && b.getZ() > b.getW() && c.getZ() > c.getW()) return;
        // return proto??e cel?? troj??heln??k je moc vlevo mimo zobrazovac?? objem
        if (a.getX() < -a.getW() && b.getX() < -b.getW() && c.getX() < -c.getW()) return;
        if (a.getY() < -a.getW() && b.getY() < -b.getW() && c.getY() < -c.getW()) return;
        if (a.getZ() < 0 && b.getZ() < 0 && c.getZ() < 0) return;
        // TODO dod??lat pro Y a Z

        // 3. se??azen?? podle Z (a.z ??? b.z ??? c.z)
        if (a.getZ() < b.getZ()) {
            var temp = a;
            a = b;
            b = temp;
        }
        if (a.getZ() < c.getZ()) {
            Vertex temp = a;
            a = c;
            c = temp;
        }
        // chyb?? zajistit b.z ??? c.z
        if (b.getZ() < c.getZ()) {
            Vertex temp = c;
            c = b;
            b = temp;
        }

//        List<Vertex> vertices = Stream
//                .of(a, b, c)
//                .sorted(Comparator.comparingDouble(Vertex::getZ))
//                .toList();

        if (a.getZ() < 0) {
            return;
        } else if (b.getZ() < 0) {
            double t12 = (0 - a.getZ()) / (b.getZ() - a.getZ());
            Vertex ab = a.mul(1 - t12).add(b.mul(t12));

            double t13 = (0 - a.getZ()) / (c.getZ() - a.getZ());
            Vertex ac = a.mul(1 - t13).add(c.mul(t13));

            drawTriangle(a, ab, ac);
        } else if (c.getZ() < 0) {
            // TODO
            drawTriangle(a, b, c);
        } else {
            drawTriangle(a, b, c);
        }
    }

    private void drawTriangle(Vertex a, Vertex b, Vertex c) {
        Optional<Vec3D> o1 = a.getPoint().dehomog();
        Optional<Vec3D> o2 = b.getPoint().dehomog();
        Optional<Vec3D> o3 = c.getPoint().dehomog();

        if (o1.isEmpty()) return;

        Vertex v1 = new Vertex(new Point3D(o1.get()), a.getColor());
        Vertex v2 = new Vertex(new Point3D(o2.get()), b.getColor()); // TODO
        Vertex v3 = new Vertex(new Point3D(o3.get()), c.getColor()); // TODO

       Vec3D vector1 = new Vec3D(v1.getPoint());
        Vec3D vector2 = new Vec3D(v2.getPoint());
        Vec3D vector3 = new Vec3D(v3.getPoint());

        // TODO transformace do okna

        vector1 = vector1.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((imageBuffer.getWidth() - 1) / 2, (imageBuffer.getHeight() - 1) / 2, 1.0));

        vector2 = vector2.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((imageBuffer.getWidth() - 1) / 2, (imageBuffer.getHeight() - 1) / 2, 1.0));

        vector3 = vector3.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((imageBuffer.getWidth() - 1) / 2, (imageBuffer.getHeight() - 1) / 2, 1.0));

        v1 = new Vertex(new Point3D(vector1),v1.getColor());
        v2 = new Vertex(new Point3D(vector2),v2.getColor());
        v3 = new Vertex(new Point3D(vector3),v3.getColor());



        // new Vec3D(v1.getPoint());

//       System.out.println(v3.getX());
//        System.out.println(v3.getY());
//        System.out.println(v2.getX());
//        System.out.println(v2.getY());

//        System.out.println(v3.getX());


//
     //   imageBuffer.setElement((int) (v1.getX() * 800), (int) (v1.getY() * 600), v1.getColor());
      //  imageBuffer.setElement((int) (v2.getX() * 800), (int) (v2.getY() * 600), v2.getColor());
      //  imageBuffer.setElement((int) (v3.getX() * 800), (int) (v3.getY() * 600), v3.getColor());


/*
        Graphics g = imageBuffer.getGraphics();
        g.setColor(new Color(1000));
      //  g.drawLine((int) v1.getX()*800, (int) v1.getY()*600, (int) v2.getX()*800, (int) v2.getY()*600);
        g.drawLine((int) v3.getX()*800,  (int) v3.getY()*600, (int) (v2.getX()*800), (int)(v2.getY()*600));
        g.drawLine((int) v2.getX()*800,  (int) v2.getY()*600, (int) (v1.getX()*800), (int)(v1.getY()*600));
        g.drawLine((int) v1.getX()*800,  (int) v1.getY()*600, (int) (v3.getX()*800), (int)(v3.getY()*600)); */

       // g.drawLine((int) a.getX(), (int) vecA.getY(), (int) vecC.getX(), (int) vecC.getY());
      //  g.drawLine((int) vecC.getX(), (int) vecC.getY(), (int) vecB.getX(), (int) vecB.getY());



        // set????dit podle Y (slide 129)
        // c??lem je V1y <= V2y <= V3y
        if (v1.getY() > v2.getY()) { // aby ve V2 bylo v??t???? Y ne?? ve V1
            Vertex temp = v1;
            v1 = v2;
            v2 = temp;
        }
        if (v2.getY() > v3.getY()) { // aby ve V3 bylo nejv??t???? Y
            Vertex temp = v2;
            v2 = v3;
            v3 = temp;
        }
        if (v1.getY() > v2.getY()) { // aby ve V1 bylo nejmen???? Y
            Vertex temp = v1;
            v1 = v2;
            v2 = temp;
        }

        // A => B
        // slide 129
        // interpolace podle Y
//        long startAB = 0;
//        if (v1.getY() > 0) startAB = v1.getY();
        long startAB = (long) Math.max(Math.ceil(v1.getY()), 0);
        double endAB = Math.min(v2.getY(), imageBuffer.getHeight() - 1);



        for (long y = startAB; y <= endAB; y++) {
            double s12 = (y - v1.getY()) / (v2.getY() - v1.getY());
            Vertex v12 = v1.mul(1 - s12).add(v2.mul(s12));

            double s13 = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = v1.mul(1 - s13).add(v3.mul(s13));

            fillLine(y, v12, v13);
        }

        long startBC = (long) Math.max(Math.ceil(v2.getY()), 0);
        double endBC = Math.min(v3.getY(), imageBuffer.getHeight() - 1);

        for (long y = startBC; y <= endBC; y++) {
            double s12 = (y - v1.getY()) / (v2.getY() - v1.getY());
            Vertex v12 = v1.mul(1 - s12).add(v2.mul(s12));

            double s13 = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex v13 = v1.mul(1 - s13).add(v3.mul(s13));

            fillLine(y, v12, v13);
        }
        // B => C
        // TODO
    }

    private void fillLine(long y, Vertex a, Vertex b) {
        if (a.getX() > b.getX()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        long start = (long) Math.max(Math.ceil(a.getX()), 0);
        double end = Math.min(b.getX(), imageBuffer.getWidth() - 1);
        for (long x = start; x <= end; x++) {
            double s = (x - a.getX()) / (b.getX() - a.getX());
            Vertex finalVertex = a.mul(1 - s).add(b.mul(s));
            Col finalColor = shader.shade(finalVertex);
            drawPixel((int) x, (int) y, finalVertex.getZ(), finalColor);
        }
    }

    public void drawPixel(int x, int y, double z, Col color) {
        Optional<Double> depthBufferElement = depthBuffer.getElement(x, y);
        if (depthBufferElement.isEmpty()) return;

        if (z < depthBufferElement.get()) {
            imageBuffer.setElement(x, y, color);
            depthBuffer.setElement(x, y, z);
        }
    }

    @Override
    public void clear() {
        imageBuffer.clear();
        depthBuffer.clear();
    }

    @Override
    public void setModel(Mat4 model) {
        this.model = model;
    }

    @Override
    public void setView(Mat4 view) {
this.view = view;
    }

    @Override
    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    @Override
    public void setShader(Shader<Vertex, Col> shader) {
        this.shader = shader;
    }
}
