import java.util.List;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import java.util.Collections;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Random;

class ConcaveStrategy extends ConvexStrategy {

  public ConcaveStrategy(){
    System.out.println("Concave Constructor");
  }

  public void drawText(VertexPolygon poly, Pane textLayer) {
    this.textLayer = textLayer;

    Random r = new Random();
    try {
      VertexList outline = poly.getDlOutline();
      rotateCounterClockwise(outline);
      Vertex[] orderedVertices = sort(outline);

      List<VerticalTrapezoid> trapezoids = getTrapezoidalDecomposition(outline, orderedVertices);
      double weightedAverageHeight = getWeightedAverageHeight(trapezoids);
      double minHeight = weightedAverageHeight * 0.5;
      VerticalTrapezoid head = trapezoids.get(0);
      VerticalTrapezoid tail = getTail(head, head, null);
      drawTrapezoid(tail);

      VertexPolygon _p = trapezoidsToPolygon(head);
      lineBreak(_p.getDlOutline(), 2);
      double m = 10;
      getRectrangles(head, poly.getText().length(), m);
      drawTrapezoids(head, null);
    } catch (Exception e){
      System.out.println("---Exception---");
      e.printStackTrace(new java.io.PrintStream(System.out));
    }
  }

  public VerticalTrapezoid getTail(VerticalTrapezoid tail, VerticalTrapezoid trap, VerticalTrapezoid prev) {
    if(!(trap instanceof VerticalTrapezoidFiller) && trap.right.start.x > tail.right.start.x) {
      tail = trap;
    }
    for(VerticalTrapezoid t : trap.getNext()) {
      if(!t.equals(prev)) {
        VerticalTrapezoid tmp = getTail(tail, t, trap);
        if(tmp.right.start.x > tail.right.start.x) tail = tmp;
      }
    }
    for(VerticalTrapezoid t : trap.getPrev()) {
      if(!t.equals(prev)) {
        VerticalTrapezoid tmp = getTail(tail, t, trap);
        if(tmp.right.start.x > tail.right.start.x) tail = tmp;
      }
    }
    return tail;
  }

  public double information(VerticalTrapezoid trap, int direction) {
    if(trap == null) return 0;

    //Laufe von links nach rechts
    if(direction == 1) {
      if(trap.informationLeft != 0) return trap.informationLeft;

      for(VerticalTrapezoid t : trap.getPrev()) {
        t.informationLeft = Math.max(information(t, direction), t.informationLeft);
      }
      trap.informationLeft += trap.area();
      for(VerticalTrapezoid t : trap.getNext()) {
        information(t, direction);
      }
      return trap.informationLeft;
    } else {
      if(trap.informationRight != 0) return trap.informationRight;

      for(VerticalTrapezoid t : trap.getNext()) {
        t.informationRight = Math.max(information(t, direction), t.informationRight);
      }
      trap.informationRight += trap.area();
      for(VerticalTrapezoid t : trap.getPrev()) {
        information(t, direction);
      }
      return trap.informationRight;
    }

  }

  // public VerticalTrapezoid trimConcave(VerticalTrapezoid head, VerticalTrapezoid trapezoid, double minHeight) {
  //   double heightLeft = trapezoid.left.height();
  //   double heightRight = trapezoid.right.height();
  //
  //   // if(!head.equals(trapezoid)) {
  //   //   return head;
  //   // }
  //
  //   if(heightLeft < heightRight) {
  //     //Sind auf linker Seite
  //     if(heightRight < minHeight) {
  //       trapezoid.getNextExplicit().removePrev(trapezoid);
  //       head = trapezoid.getNextExplicit();
  //       return trimConvex(head, trapezoid.getNextExplicit(), minHeight);
  //     } else if(heightLeft < minHeight) {
  //       double slope = Math.abs(trapezoid.bot.slope() - trapezoid.top.slope());
  //       double b = trapezoid.left.height();
  //       double x = (minHeight - b) / slope;
  //       double offset = x + trapezoid.left.start.x;
  //       LineSegment line = new LineSegment(offset, 0, offset, 1000);
  //       Vertex top = new VirtualVertex(-1,-1);
  //       Vertex bot = new VirtualVertex(-1,-1);
  //       line.getLineIntersection(trapezoid.top, top);
  //       line.getLineIntersection(trapezoid.bot, bot);
  //       trapezoid.bot = new LineSegment(bot, trapezoid.bot.end);
  //       trapezoid.top = new LineSegment(top, trapezoid.top.end);
  //       trapezoid.left = new LineSegment(top, bot);
  //
  //       if(trapezoid.getNextExplicit() != null) {
  //         return trimConvex(head, trapezoid.getNextExplicit(), minHeight);
  //       } else return head;
  //     } else {
  //       if(trapezoid.getNextExplicit() != null) {
  //         return trimConvex(head, trapezoid.getNextExplicit(), minHeight);
  //       } else return head;
  //     }
  //
  //   } else if(heightLeft > heightRight) {
  //     //Sind auf rechten Seite
  //     if(heightLeft < minHeight) {
  //       VerticalTrapezoid prev = trapezoid.getPrevExplicit();
  //       if(prev != null)
  //         prev.removeNext(trapezoid);
  //       return head;
  //     } else if(heightRight < minHeight) {
  //       double slope = - Math.abs(trapezoid.bot.slope() - trapezoid.top.slope());
  //       double b = trapezoid.left.height();
  //       double x = (minHeight - b) / slope;
  //
  //       double offset = x + trapezoid.left.start.x;
  //       LineSegment line = new LineSegment(offset, 0, offset, 1000);
  //       Vertex top = new VirtualVertex(-1,-1);
  //       Vertex bot = new VirtualVertex(-1,-1);
  //       line.getLineIntersection(trapezoid.top, top);
  //       line.getLineIntersection(trapezoid.bot, bot);
  //       trapezoid.bot = new LineSegment(trapezoid.bot.start, bot);
  //       trapezoid.top = new LineSegment(trapezoid.top.start, top);
  //       trapezoid.right = new LineSegment(top, bot);
  //       trapezoid.removeNext(trapezoid.getNextExplicit());
  //       return head;
  //     } else {
  //       if(trapezoid.getNextExplicit() != null) {
  //         return trimConvex(head, trapezoid.getNextExplicit(), minHeight);
  //       } else return head;
  //     }
  //   } else {
  //     if(trapezoid.getNextExplicit() != null) {
  //       return trimConvex(head, trapezoid.getNextExplicit(), minHeight);
  //     } else return head;
  //   }
  // }

}
