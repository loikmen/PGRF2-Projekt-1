package renderer;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.List;

public class Renderer3D implements GPURenderer {

    private Mat4 model = new Mat4Identity();
    private Mat4 view = new Mat4Identity();
    private Mat4 projection = new Mat4Identity();

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

    }

    @Override
    public void clear() {

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
}
