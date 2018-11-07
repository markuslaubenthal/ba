import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;

class VertexPolygon {
  protected ArrayList<Vertex> outline;
  protected String text = "";


  public VertexPolygon() {
    outline = new ArrayList<Vertex>();
  }

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
    for(Vertex v : outline) {
      poly.getPoints().addAll(new Double[] {v.x, v.y} );
    }
    poly.setFill(Color.color(0,0,0,0));
    poly.setStrokeWidth(2);
    poly.setStroke(Color.BLACK);
    polyLayer.getChildren().add(poly);
  }

  public void drawText(Pane textLayer){
    double leftest = 1000;
    double rightest = 0;
    double highest = 1000;
    double lowest = 0;
    for(Vertex v : outline){
      leftest = Math.min(leftest, v.x);
      rightest = Math.max(rightest, v.x);
      highest = Math.min(highest, v.y);
      lowest = Math.max(lowest, v.y);
    }
    double voulume = (rightest - leftest) * (lowest - highest);
    int fontsize = (int) (Math.sqrt(voulume/this.text.length()) * 0.7);

    double x = leftest + (rightest - leftest) * (0.3 / 2);
    double y = highest + fontsize + (lowest - highest) * (0.3 / 2);

    Text t = new Text();
    t.setFont(new Font(fontsize));
    t.setText(this.text);
    t.setWrappingWidth((rightest-leftest) * 0.7);
    t.setTextAlignment(TextAlignment.JUSTIFY);
    t.setX(x);
    t.setY(y);
    textLayer.getChildren().add(t);

  }


}
