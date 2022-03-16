package renderer;

import model.Part;
import model.Vertex;
import shader.Shader;
import transforms.Col;
import transforms.Mat4;

import java.util.List;

public interface GPURenderer {

    void draw(List<Part> partsBuffer, List<Integer> indexBuffer, List<Vertex> vertexBuffer);

    void clear();

    void setModel(Mat4 model);

    void setView(Mat4 view);

    void setProjection(Mat4 projection);

    void setShader(Shader<Vertex, Col> shader);

}
