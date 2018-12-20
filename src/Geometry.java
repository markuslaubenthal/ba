import java.util.ArrayList;
import java.util.Hashtable;
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

  public static ArrayList<Bottleneck> findBottleneckInPolygon(VertexPolygon p, double minWidth) {
    ArrayList<Bottleneck> bottleneckList = new ArrayList<Bottleneck>();
    for(Vertex v : p.getOutline()) {
      Hashtable<String, LineSegment> edgeTable = new Hashtable<String, LineSegment>();
      ArrayList<Vertex> intersections = new ArrayList<Vertex>();
      LineSegment horizontalLine = new LineSegment(0, v.y, 1000, v.y);
      for(int i = 0; i < p.getOutline().size(); i++){
        LineSegment edge = p.getLineSegment(i);
        Vertex intersection = new Vertex(-1,-1);
        if(edge.getLineIntersection(horizontalLine, intersection) || v.y == edge.start.y || v.y == edge.end.y) {
          if(intersection.x == -1 && intersection.y == -1){
            if(v.y == edge.start.y) intersection = edge.start;
            else intersection = edge.end;
            edgeTable.put(intersection.toString(), new LineSegment(intersection, intersection));
          } else {
            edgeTable.put(intersection.toString(), edge);
          }

          intersections.add(intersection);
        }
      }
      intersections.sort(new VertexXComparator());
      for(int i = 0; i < intersections.size() - 2; i += 2){
        if(intersections.get(i).equals(v) || intersections.get(i + 1).equals(v)) {
          double width = Math.abs(intersections.get(i).x - intersections.get(i + 1).x);
          if(width < minWidth && width > 0) {
            if(intersections.get(i).equals(v)){
              bottleneckList.add(new Bottleneck(new LineSegment(v, intersections.get(i + 1)), edgeTable.get(intersections.get(i + 1).toString())));
            } else {
              bottleneckList.add(new Bottleneck(new LineSegment(v, intersections.get(i)), edgeTable.get(intersections.get(i).toString())));
            }
          }
          // break;
        }
      }
    }
    return bottleneckList;
  }

  public static ArrayList slicePolygon(VertexPolygon poly, ArrayList<Bottleneck> bottleneckList) {
    return null;
  }

  public static VertexPolygon[] splitPolygon(VertexPolygon poly, Bottleneck bottleneck) {
    ArrayList<Vertex> outline = poly.getOutline();
    int polyLineStart = outline.indexOf(bottleneck.polygonLine.start);
    int polyLineEnd = outline.indexOf(bottleneck.polygonLine.end);
    int neckStart = outline.indexOf(bottleneck.neckLine.start);
    int neckEnd = outline.indexOf(bottleneck.neckLine.end);

    VertexPolygon upper = new VertexPolygon();
    VertexPolygon lower = new VertexPolygon();

    Vertex v = outline.get(neckStart);
    upper.addVertex(v);
    lower.addVertex(v);
    for(int i = 1; true; i++) {
      int index = Math.floorMod((neckStart + i), outline.size());
      v = outline.get(index);

      upper.addVertex(v);
      if(index == polyLineStart || index == polyLineEnd) {
        if(!bottleneck.neckLine.end.equals(bottleneck.polygonLine.end)) {
          upper.addVertex(bottleneck.neckLine.end);
        }
        break;
      }
    }

    for(int i = -1; true; i--) {
      int index = Math.floorMod((neckStart + i), outline.size());
      v = outline.get(index);

      lower.addVertex(v);
      if(index == polyLineStart || index == polyLineEnd) {
        if(!bottleneck.neckLine.end.equals(bottleneck.polygonLine.end)) {
          lower.addVertex(bottleneck.neckLine.end);
        }
        break;
      }
    }

    return new VertexPolygon[]{upper, lower};

  }
}
