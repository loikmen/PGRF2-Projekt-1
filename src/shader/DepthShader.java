package shader;

import model.Vertex;
import transforms.Col;

public class DepthShader implements Shader<Vertex, Col> {

    @Override
    public Col shade(Vertex vertex) {
        double z = 1 - vertex.getZ();
        return new Col(z, z, z);
    }

}
