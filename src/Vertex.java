import javafx.scene.shape.Circle;

/**
 * @author Markus Laubenthal
 * @author Lennard Alms
 * Ein zweidimensionaler Punkt
 */
public class Vertex {
    public double x;
    public double y;
    public Circle point;

    public Vertex(double x, double y, Circle point) {
        this.x = x;
        this.y = y;
        this.point = point;
    }

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
        point = null;
    }

    public void setPoint(Circle point) {
      this.point = point;
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

    public boolean equals(Object v) {

      if (v instanceof Vertex){
          Vertex ptr = (Vertex) v;
          if(ptr.x == this.x && ptr.y == this.y) return true;
      }
      return false;
    }

}
