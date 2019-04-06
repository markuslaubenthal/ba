import javafx.scene.shape.Circle;

class VirtualVertex extends Vertex {
  public VirtualVertex(Vertex v) {
    super(v.x, v.y);
  }
  public VirtualVertex(double x, double y, Circle point) {
      super(x,y,point);
  }

  public VirtualVertex(double x, double y) {
      super(x,y);
  }
}
