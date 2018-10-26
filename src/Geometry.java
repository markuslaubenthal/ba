import java.util.ArrayList;

class Geometry {

  public static double getAreaSizeOfPolygon(VertexPolygon p) {
    ArrayList<Vertex> outline = p.getOutline();
    return getAreaSizeOfPolygon(outline);
  }

  public static double getAreaSizeOfPolygon(ArrayList<Vertex> outline) {
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
}
