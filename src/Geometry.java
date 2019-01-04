import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;

class Geometry {

  public static VertexPolygon scalePolygon(VertexPolygon originalPoly, double factor) {

    /*
    Calculate the center of the polygon.
     */

    Vertex centerV = new Vertex(0,0);

    for(Vertex v : originalPoly.getOutline()) {
      centerV = centerV.add(v);

    }

    centerV = centerV.mult(1.0/originalPoly.getOutline().size());

    /*
    Create the new polygon by moving the center to the origin,
    scaling and moving the center back.
     */

    VertexPolygon poly = new VertexPolygon();
    poly.text = originalPoly.text;

    for(Vertex v : originalPoly.getOutline()) {
      poly.addVertex(v.sub(centerV).mult(factor).add(centerV));
    }

    return poly;

  }

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



  public static VertexPolygon[] findSplitLineApprox(VertexPolygon poly, double upperRatio, double lowerRatio) {

    Hashtable<String,LineSegment> edgeTable = new Hashtable<String,LineSegment>();
    Hashtable<String,Double> scoreTable = new Hashtable<String,Double>();
    int density = 8;
    ArrayList<Vertex> pointList = buildOutlinePoints(poly, edgeTable, density);
    ArrayList<LineSegment> bottleneckList = new ArrayList<LineSegment>();
    double polygonSize = poly.getAreaSize();
    for(Vertex v : pointList) {
      for(Vertex w : pointList) {
        if(v.y < w.y && Math.abs(Math.atan2(w.y - v.y,w.x - v.x)) < Math.PI / 20){
          if(canSee(poly,v,w) && !edgeTable.get(v.toString()).equals(edgeTable.get(w.toString()))){
            VertexPolygon[] subPolys = splitPolygon(poly,v,edgeTable.get(v.toString()),w,edgeTable.get(w.toString()));
            bottleneckList.add(new LineSegment(v,w));
            double score = Math.abs(subPolys[0].getAreaSize() / polygonSize - upperRatio) + Math.abs(subPolys[1].getAreaSize() / polygonSize - lowerRatio);
            scoreTable.put(new LineSegment(v,w).toString(), score);
          }
        }
      }
    }
    sortBottleneckList(bottleneckList, scoreTable);
    return splitPolygon(poly,bottleneckList.get(0).start,edgeTable.get(bottleneckList.get(0).start.toString()),bottleneckList.get(0).end,edgeTable.get(bottleneckList.get(0).end.toString()));
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

    /*
    We now need to check if the upper and lower polygons are named correctly.
    We are assuming that the bottleneck line from v to w goes from left to right (v.y < w.y).
    Also we are assuming that v is not equal to either vEdge.start or vEdge.end.
    We know that the upper polygon is left of the vector from v to w.
    So we can determin which point of vEdge belongs to the upper polygon
    by looking at the angle between v->w and v->vEdge.(start / end).
    Is the angle smaller than pi the point belongs to the upper polygon.
    This does not work in all cases. Wide rectangles with a downward facing splitline
    will be categorized wrong. In these cases the splitting angle needs to be smaller or something.
    Not sure how to fix this.
     */

    Vertex vTOw = w.sub(v);
    Vertex vTOvEdgeStart = vEdge.start.sub(v);

    double angle = Math.atan2(vTOvEdgeStart.y, vTOvEdgeStart.x) - Math.atan2(vTOw.y, vTOw.x);
    if(angle < 0) angle += 2 * Math.PI;

    if(angle > Math.PI) {
      if(upper.contains(vEdge.start)) {
        return new VertexPolygon[]{upper, lower};
      } else {
        return new VertexPolygon[]{lower, upper};
      }
    } else {
      if(upper.contains(vEdge.start)) {
        return new VertexPolygon[]{lower, upper};
      } else {
        return new VertexPolygon[]{upper, lower};
      }
    }

  }

  public static VertexPolygon[] splitPolygonOnBestBottleneck(VertexPolygon poly) {
    double minWidth = 0.5 * Math.sqrt(poly.getAreaSize());

    ArrayList<Bottleneck> bottlenecks = findBottleneckInPolygon(poly, minWidth);
    if(bottlenecks.size() > 0) {
      Bottleneck best = bottlenecks.get(0);
      double bestScore = 0;
      for(int i = 0; i < bottlenecks.size(); i++) {
        Bottleneck b = bottlenecks.get(i);
        VertexPolygon[] subPolys = splitPolygon(poly, b.neckLine.start, b.neckLine, b.neckLine.end, b.polygonLine);
        double score = b.neckLine.start.distance(b.neckLine.end) * Math.sqrt(Math.max(subPolys[0].getAreaSize(), subPolys[1].getAreaSize()));
        bestScore = Math.max(score, bestScore);
        if(bestScore == score) best = b;
        // double score = v.distance(w) * Math.sqrt(Math.min(subPolys[0].getAreaSize(), subPolys[1].getAreaSize()));
      }
      VertexPolygon[] subPolys = splitPolygon(poly, best.neckLine.start, best.neckLine, best.neckLine.end, best.polygonLine);
      return subPolys;
    } else {
      return new VertexPolygon[]{poly};
    }
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


  public static ArrayList<Vertex> buildOutlinePoints(VertexPolygon poly, Hashtable<String, LineSegment> table, int density) {
    ArrayList<Vertex> pointList = new ArrayList<Vertex>();
    for(int i = 0; i < poly.getOutline().size(); i++) {
      LineSegment edge = poly.getLineSegment(i);
      Vertex directionV = edge.end.sub(edge.start).mult(1/edge.end.distance(edge.start));
      for(int j = 1; j < density; j++) {
        Vertex newPoint = edge.start.add(directionV.mult(j * edge.end.distance(edge.start)/density));
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
