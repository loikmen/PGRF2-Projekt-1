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
        // 1. transformace vrcholů
        Vertex a = new Vertex(
                v1.getPoint().mul(model).mul(view).mul(projection),
                v1.getColor()
        );
        // TODO transformovat ostatní vrcholy
        Vertex b = v2;
        Vertex c = v3;

        // 2. ořezání
        // ořezat ty trojúhelníky, které jsou celé mimo zobrazovací objem
        if (a.getX() > a.getW() && b.getX() > b.getW() && c.getX() > c.getW()) return;
        // return protože celý trojúhelník je moc vlevo mimo zobrazovací objem
        if (a.getX() < -a.getW() && b.getX() < -b.getW() && c.getX() < -c.getW()) return;
        // TODO dodělat pro Y a Z

        // 3. seřazení podle Z (a.z ≥ b.z ≥ c.z)
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
        // chybí zajistit b.z ≥ c.z
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
        } else {
            drawTriangle(a, b, c);
        }
    }

    private void drawTriangle(Vertex a, Vertex b, Vertex c) {
        Optional<Vec3D> o1 = a.getPoint().dehomog();

        if (o1.isEmpty()) return;

        Vertex v1 = new Vertex(new Point3D(o1.get()), a.getColor());
        Vertex v2 = b; // TODO
        Vertex v3 = c; // TODO

        // TODO transformace do okna
        // new Vec3D(v1.getPoint());

//        System.out.println(v1.getX());
//        System.out.println(v2.getX());
//        System.out.println(v3.getX());
//
//        imageBuffer.setElement((int) (v1.getX() * 10), (int) (v1.getY() * 10), v1.getColor());

        // setřídit podle Y (slide 129)
        // cílem je V1y <= V2y <= V3y
        if (v1.getY() > v2.getY()) { // aby ve V2 bylo větší Y než ve V1
            Vertex temp = v1;
            v1 = v2;
            v2 = temp;
        }
        if (v2.getY() > v3.getY()) { // aby ve V3 bylo největší Y
            Vertex temp = v2;
            v2 = v3;
            v3 = temp;
        }
        if (v1.getY() > v2.getY()) { // aby ve V1 bylo nejmenší Y
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

    private void drawPixel(int x, int y, double z, Col color) {
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

    }

    @Override
    public void setView(Mat4 view) {

    }

    @Override
    public void setProjection(Mat4 projection) {

    }

    @Override
    public void setShader(Shader<Vertex, Col> shader) {
        this.shader = shader;
    }
}
