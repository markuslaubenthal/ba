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
  /*
  public static ArrayList<Bottleneck> findBottleneckInPolygon2(VertexPolygon poly) {
    ArrayList<Vertex> outline = poly.getOutline();
    ArrayList<Vertex> _outline = new ArrayList<Vertex>();
    for(Vertex v : outline){
      for(int i = 0; i < outline.size; i++){
          LineSegment edge = poly.getLineSegment(i);
          LineSegment tmpOrthogonal = new LineSegment(edge.start.y, -1 * edge.start.x, edge.end.y, -1 * edge.end.x);
          Vertex direction = tmpOrthogonal.start.sub(orthogonal.end);
          direction = direction.mult(1/direction.mag()).mult(1000);
          LineSegment orthogonal = new LineSegment(v.add(direction), v.add(direction.mult(-1)));

      }
    }

  }*/


  public static ArrayList<LineSegment> findAllBottlenecksApprox(VertexPolygon poly) {
    double[] boundingBox = poly.getBoundingBox();
    double diagonal = new Vertex(boundingBox[0],boundingBox[3]).sub(new Vertex(boundingBox[1],boundingBox[2])).mag();

    double minWidth = 4 * Math.sqrt(diagonal);
    double minArea = 0.33 * poly.getAreaSize();

    Hashtable<String,LineSegment> edgeTable = new Hashtable<String,LineSegment>();
    ArrayList<Vertex> pointList = buildOutlinePoints(poly,edgeTable);
    ArrayList<LineSegment> bottleneckList = new ArrayList<LineSegment>();

    for(Vertex v : pointList) {
      for(Vertex w : pointList) {
        if(v.distance(w) < minWidth && canSee(poly,v,w) && !edgeTable.get(v.toString()).equals(edgeTable.get(w.toString()))){
          VertexPolygon[] subPolys = splitPolygon(poly,v,edgeTable.get(v.toString()),w,edgeTable.get(w.toString()));
          if(subPolys[0].getAreaSize() > minArea && subPolys[1].getAreaSize() > minArea){
            bottleneckList.add(new LineSegment(v,w));
          }
        }
      }
    }
    return bottleneckList;
  }

  public static VertexPolygon[] splitPolygon(VertexPolygon poly, Vertex v, LineSegment vEdge, Vertex w, LineSegment wEdge) {
    VertexPolygon upper = new VertexPolygon();
    VertexPolygon lower = new VertexPolygon();
    Boolean lowerArc = true;
    for(Vertex u : poly.getOutline()) {
      if(!u.equals(vEdge.start) && !u.equals(wEdge.start)) {
        if(lowerArc) lower.addVertex(u);
        if(!lowerArc) upper.addVertex(u);
      } else if(u.equals(vEdge.start)) {
        if(lowerArc) {
          lower.addVertex(u);
          lower.addVertex(v);
          upper.addVertex(v);
          lowerArc = false;
        } else {
          upper.addVertex(u);
          upper.addVertex(v);
          lower.addVertex(v);
          lowerArc = true;
        }
      } else if(u.equals(wEdge.start)) {
        if(lowerArc) {
          lower.addVertex(u);
          lower.addVertex(w);
          upper.addVertex(w);
          lowerArc = false;
        } else {
          upper.addVertex(u);
          upper.addVertex(w);
          lower.addVertex(w);
          lowerArc = true;
        }
      }
    }
    return new VertexPolygon[]{upper, lower};
  }

  public static ArrayList<Vertex> buildOutlinePoints(VertexPolygon poly, Hashtable<String, LineSegment> table) {
    ArrayList<Vertex> pointList = new ArrayList<Vertex>();
    for(int i = 0; i < poly.getOutline().size(); i++) {
      LineSegment edge = poly.getLineSegment(i);
      Vertex directionV = edge.end.sub(edge.start).mult(1/edge.end.distance(edge.start));
      for(int j = 1; j < edge.end.distance(edge.start); j++) {
        Vertex newPoint = edge.start.add(directionV.mult(j));
        pointList.add(newPoint);
        table.put(newPoint.toString(),edge);
      }
    }
    return pointList;
  }


  public static Boolean canSee(VertexPolygon poly, Vertex v, Vertex w) {
    LineSegment visionLine = new LineSegment(v,w);
    Vertex intersection = new Vertex(-1,-1);
    for(int i = 0; i < poly.getOutline().size(); i++) {
      LineSegment edge = poly.getLineSegment(i);
      visionLine.getLineIntersection(edge, intersection);
      if(intersection.x != -1) {
        if(intersection.x != v.x && intersection.y != v.y || intersection.x != w.x && intersection.y != w.y) {
          return false;
        }
      }
    }
    return true;
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

  public static ArrayList<Vertex> slicePolygon(VertexPolygon poly, ArrayList<Bottleneck> bottleneckList) {
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

  public static Vertex[] getFurthestPointsInPolygon(VertexPolygon polygon) {
    Vertex x = null;
    Vertex y = null;
    double farthestDistance = 0;
    ArrayList<Vertex> outline = polygon.getOutline();
    for(int i = 0; i < outline.size(); i++) {
      for(int j = 0; j < outline.size(); j++) {
        double distance = outline.get(i).distance(outline.get(j));
        if(distance > farthestDistance) {
          farthestDistance = distance;
          x = outline.get(i);
          y = outline.get(j);
        }
      }
    }
    return new Vertex[]{x, y};
  }
}
