import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;

class VertexPolygon {
  protected ArrayList<Vertex> outline;
  protected String text = "";

  public VertexPolygon(String text) {
    outline = new ArrayList<Vertex>();
    this.text = text;
  }

  public ArrayList<Vertex> getOutline() {
    return outline;
  }

  public void addVertex(Vertex v) {
    outline.add(v);
  }

  public boolean contains(Vertex v) {
    return outline.contains(v);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gaußsche Trapezformel
   * @method getAreaSize
   * @return [description]
   */
  public double getAreaSize() {
    return Geometry.getAreaSizeOfPolygon(this);
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

  public void drawText(Pane textLayer){
    double x = 0;
    double y = 0;
    for(Vertex v : outline){
      x += v.x;
      y += v.y;
    }
    x = x / outline.size();
    y = y / outline.size();
    Text t = new Text();
    t.setFont(new Font(20));
    t.setText(this.text);
    t.setX(x);
    t.setY(y);
    textLayer.getChildren().add(t);
  }


}
