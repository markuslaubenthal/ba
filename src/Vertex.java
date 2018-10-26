/**
 * @author Markus Laubenthal
 * @author Lennard Alms
 * Ein zweidimensionaler Punkt
 */
public class Vertex {
    public double x;
    public double y;

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Berechnet die Distanz zwischen 2 Vertices.
     * @method distance
     */
    public double distance(Vertex v) {
        double dist = sub(v).mag();
        return dist;
    }

    /**
     * Subtrahiert den übergebenen Vertex von sich selbst.
     * @method sub
     */
    public Vertex sub(Vertex vertex) {
        return new Vertex(this.x - vertex.x, this.y - vertex.y);
    }

    /**
     * Addiert den übergebenen Vertex auf sich selbst.
     * @method add
     */
    public Vertex add(Vertex vertex) {
        return new Vertex(this.x + vertex.x, this.y + vertex.y);
    }

    /**
     * Multipliziert den Vertex mit einem Skalar
     * @method mult
     */
    public Vertex mult(double scalar) {
        return new Vertex(this.x * scalar, this.y * scalar);
    }

    /**
     * Berechnet die Länge des Vertex
     * @method mag
     */
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Berechnet das Skalarprodukt von 2 Vektoren
     * @method dot
     */
    public double dot(Vertex vertex) {
        return this.x * vertex.x + this.y * vertex.y;
    }

    /**
     * Berechnet das Kreuzprodukt von 2 Vektoren
     * @method cross
     */
    public double cross(Vertex vertex) {
        return this.y * vertex.x - this.x * vertex.y;
    }

}
