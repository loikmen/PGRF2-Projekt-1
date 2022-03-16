package model;

import transforms.Col;
import transforms.Point3D;

public class Vertex {

    private final Point3D point;
    private final Col color;

    // souřadnice do textury pro mapování obrázku na plochu
    // private final Vec2D textureCoord;

    // normála pro potřeby osvětlení
    // private final Vec3D normal;

    public Vertex(Point3D point, Col color) {
        this.point = point;
        this.color = color;
    }

    public Vertex mul(double t) {
        return new Vertex(
                point.mul(t),
                color.mul(t)
        );
    }

    public Vertex add(Vertex otherVertex) {
        return new Vertex(
                point.add(otherVertex.getPoint()),
                color.add(otherVertex.getColor())
        );
    }

    // TODO? dehomog()

    public Point3D getPoint() {
        return point;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public double getZ() {
        return point.getZ();
    }

    public double getW() {
        return point.getW();
    }

    public Col getColor() {
        return color;
    }
}
