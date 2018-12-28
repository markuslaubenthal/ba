import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;

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



  public static ArrayList<LineSegment> findAllBottlenecksApprox(VertexPolygon poly) {
    double[] boundingBox = poly.getBoundingBox();
    double diagonal = new Vertex(boundingBox[0],boundingBox[3]).sub(new Vertex(boundingBox[1],boundingBox[2])).mag();

    double minWidth = 0.2 * diagonal;
    double minArea =  0.2 * poly.getAreaSize();

    Hashtable<String,LineSegment> edgeTable = new Hashtable<String,LineSegment>();
    Hashtable<String,Double> scoreTable = new Hashtable<String,Double>();
    ArrayList<Vertex> pointList = buildOutlinePoints(poly,edgeTable);
    ArrayList<LineSegment> bottleneckList = new ArrayList<LineSegment>();

    for(Vertex v : pointList) {
      for(Vertex w : pointList) {
        if(v.distance(w) < minWidth && canSee(poly,v,w) && !edgeTable.get(v.toString()).equals(edgeTable.get(w.toString()))){
          VertexPolygon[] subPolys = splitPolygon(poly,v,edgeTable.get(v.toString()),w,edgeTable.get(w.toString()));
          if(subPolys[0].getAreaSize() > minArea && subPolys[1].getAreaSize() > minArea){
            bottleneckList.add(new LineSegment(v,w));
            double score = v.distance(w) * Math.sqrt(Math.min(subPolys[0].getAreaSize(), subPolys[1].getAreaSize()));
            scoreTable.put(new LineSegment(v,w).toString(), score);
          }
        }
      }
    }
    sortBottleneckList(bottleneckList, scoreTable);
    return bottleneckList;
  }

  public static void sortBottleneckList(ArrayList<LineSegment> bottleneckList, Hashtable<String,Double> scoreTable){
    Collections.sort(bottleneckList, new Comparator<LineSegment>() {
        @Override
        public int compare(LineSegment line1, LineSegment line2)
        {
            return (int) Math.signum(scoreTable.get(line1.toString()) - scoreTable.get(line2.toString()));
        }
    });
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
    eliminateDuplicates(upper);
    eliminateDuplicates(lower);
    return new VertexPolygon[]{upper, lower};
  }

  public static void eliminateDuplicates(VertexPolygon poly) {
    HashSet<Vertex> checklist = new HashSet<Vertex>();
    ArrayList<Vertex> newOutline = new ArrayList<Vertex>();
    for(Vertex v : poly.getOutline()) {
      if(!checklist.contains(v)) {
        newOutline.add(v);
        checklist.add(v);
      }
    }
    poly.outline = newOutline;
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
    if(v.equals(w)) return false;
    LineSegment visionLine = new LineSegment(v,w);
    Vertex intersect = new Vertex(-1,-1);
    for(int i = 0; i < poly.getOutline().size(); i++) {
      LineSegment edge = poly.getLineSegment(i);
      if(visionLine.getLineIntersection(edge, intersect)) {
        if(!intersect.equals(v) && !intersect.equals(w)) {
          return false;
        }
      }
    }
    if(!poly.vertexInPolygon(v.add(w.sub(v).mult(0.5)))) return false;
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
        if(edge.getLineIntersection(horizontalLine, intersection)){
          double width = Math.abs(v.x - intersection.x);
          if(canSee(p,v,intersection) && width < minWidth && width > 0){
            intersections.add(intersection);
            bottleneckList.add(new Bottleneck(new LineSegment(v, intersection), edge));
          }
        }
      }
    }
    return bottleneckList;
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
