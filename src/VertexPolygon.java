import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;

class VertexPolygon {
  protected ArrayList<Vertex> outline;
  protected String text = "";
  protected TextStrategy strategy;


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
    if(!this.contains(v)){
      outline.add(v);
    }
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



  public void setTextStrategy(TextStrategy strategy){
    this.strategy = strategy;
  }

  public void drawText(Pane textLayer){
    strategy.drawText(this, textLayer);
  }

  public LineSegment getLineSegment(int index) {
    if(index < outline.size()) {
      Vertex start;
      Vertex end;
      start = outline.get(index);
      if(index + 1 < outline.size()) {
        end = outline.get(index + 1);
      } else {
        end = outline.get(0);
      }
    }
    return new LineSegment(start, end);
  }
  return null;


}
