import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import javafx.scene.shape.Line;
import java.lang.Math;
import javafx.scene.paint.Color;
import java.util.Collections;
import org.apache.commons.math;


class CenterXStrategy implements TextStrategy{


  public CenterXStrategy() {}


  public void drawText(VertexPolygon originalPoly, Pane textLayer){

    ArrayList<Vertex> ereignisstruktur = new ArrayList<>();
    ereignisstruktur.addAll(originalPoly.getOutline());
    ArrayList<LineSegment> outlineSegments = originalPoly.getLineSegments();
    Collections.sort(ereignisstruktur, new VertexXComparator());
    double e = 0.5;

    ArrayList<LineSegment> bisectorSegments = new ArrayList<LineSegment>();

    for(Vertex v : ereignisstruktur) {
      System.out.println(v.x);
    }



    for(int i = 0; i < ereignisstruktur.size() - 1; i++) {
      Vertex v = ereignisstruktur.get(i);
      Vertex u = ereignisstruktur.get(i+1);
      if(Math.abs(u.x - v.x) < 2) continue;

      LineSegment leftLine = new LineSegment(v.x + e, 0, v.x + e , 1000);
      LineSegment rightLine = new LineSegment(u.x - e, 0, u.x - e , 1000);
      MALineSegment leftMALine = new MALineSegment(new MAVertex(leftLine.start.x, leftLine.start.y), new MAVertex(leftLine.end.x, leftLine.end.y));
      MALineSegment rightMALine = new MALineSegment(new MAVertex(rightLine.start.x, rightLine.start.y), new MAVertex(rightLine.end.x, rightLine.end.y));

      ArrayList<Vertex> cutListL = new ArrayList<Vertex>();
      ArrayList<Vertex> cutListR = new ArrayList<Vertex>();

      for(LineSegment l : outlineSegments) {
        Vertex cutL = new Vertex(0,0);
        Vertex cutR = new Vertex(0,0);
        if(leftLine.getLineIntersection(l, cutL)) cutListL.add(cutL);
        if(rightLine.getLineIntersection(l, cutR)) cutListR.add(cutR);
      }

      Collections.sort(cutListL, new VertexYComparator());
      Collections.sort(cutListR, new VertexYComparator());

      for(int j = 0; j < cutListL.size(); j += 2) {
        LineSegment upper = new LineSegment(cutListL.get(j), cutListR.get(j));
        LineSegment lower = new LineSegment(cutListL.get(j + 1), cutListR.get(j + 1));
        /*
        MALineSegment bisection = Geometry.calcBisec(upper, lower);
        MAVertex intersectLeft = new MAVertex(0,0);
        MAVertex intersectRight = new MAVertex(0,0);
        leftMALine.getLineIntersection(bisection, intersectLeft);
        rightMALine.getLineIntersection(bisection, intersectRight);
        intersectLeft.y =
        LineSegment bisector = new LineSegment(intersectLeft.x, intersectLeft.y, intersectRight.x, intersectRight.y);
        */

        double y1 = (upper.start.y + lower.start.y) / 2;
        double y2 = (upper.end.y + lower.end.y) / 2;
        LineSegment bisector = new LineSegment(upper.start.x, y1, upper.end.x, y2);
        bisectorSegments.add(bisector);

      }

    }

    for(LineSegment l : bisectorSegments) {
      Line line = new Line(l.start.x, l.start.y, l.end.x, l.end.y);
      textLayer.getChildren().add(line);
    }




  }




}
