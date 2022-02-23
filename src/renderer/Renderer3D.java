package renderer;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Mat4;

import java.util.List;

public class Renderer3D implements GPURenderer {

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
                    drawTriangle(vertex1, vertex2, vertex3);
                }
            }
//            else if (topologyType == TopologyType.LINE) {
//
//            } ...
        }
    }

    private void drawTriangle(Vertex v1, Vertex v2, Vertex v3) {

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
