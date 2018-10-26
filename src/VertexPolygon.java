import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.Pane;

class VertexPolygon {
  protected ArrayList<Vertex> outline;

  public VertexPolygon() {
    outline = new ArrayList<Vertex>();
  }

  public void addVertex(Vertex v) {
    outline.add(v);
  }

  public boolean contains(Vertex v) {
    return outline.contains(v);
  }

  /**
   * Gaußsche Trapezformel
   * @method getAreaSize
   * @return [description]
   */
  public double getAreaSize() {
    double area = 0;
    Vertex v;
    Vertex w;
    for(int i = 0; i < outline.size() - 1; i++) {
      v = outline.get(i);
      w = outline.get(i + 1);
      area += (v.x * w.y - v.y * w.x);
    }
    Vertex first = outline.get(0);
    Vertex last = outline.get(outline.size() - 1);
    area += last.x * first.y - last.y * first.x;
    area /= 2;
    return Math.abs(area);
  }

  public void colorizeVertecies(double r, double g, double b, double opacity) {
    for(Vertex v : outline){
      v.point.setFill(Color.color(r,g,b,opacity));
    }
  }

  public void drawPolygon(Pane polyLayer){
    Polygon poly = new Polygon();
    for(Vertex v : outline) { poly.getPoints().addAll(new Double[] {v.x, v.y} ); }
    poly.setFill(Color.color(0,0,0,0));
    poly.setStrokeWidth(2);
    poly.setStroke(Color.BLACK);
    polyLayer.getChildren().add(poly);
  }


}
