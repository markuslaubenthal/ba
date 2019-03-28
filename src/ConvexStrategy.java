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
import java.util.HashMap;
import java.util.Arrays;

class ConvexStrategy implements TextStrategy{
  double fontsize;
  Pane textLayer;


  public ConvexStrategy(){
    System.out.println("Convex Constructor");
  }

  // public List<LineSegment> getVerticalIntersections(VertexPolygon polygon, double x) {
  //
  // }

  public void drawText(VertexPolygon poly, Pane textLayer) {
    this.textLayer = textLayer;

    try {
      VertexList outline = poly.getDlOutline();
      Vertex[] orderedVertices = sort(outline);
      getTrapezoidalDecomposition(outline, orderedVertices);


      // double area[] = getUsableArea(poly);
      // double width = area[1] - area[0];
      // String text = poly.getText();
      //
      // double fontwidth = width / text.length();
      // System.out.println("Fontwidth: " + fontwidth);


    } catch (Exception e){
      e.printStackTrace(new java.io.PrintStream(System.out));
    }
  }

  public Vertex[] sort(VertexList hashset) {
    Vertex[] v = (Vertex[])hashset.toArray();
    Arrays.sort(v, new VertexXComparator());
    return v;
  }


  public void getTrapezoidalDecomposition(VertexList outline, Vertex[] orderedVertices) {

    int size = orderedVertices.length;
    //Active Trapezoid Tree
    TreeSet<ProxyVerticalTrapezoidVertex> att = new TreeSet<ProxyVerticalTrapezoidVertex>(new VerticalTrapezoidVertexComparator());
    VerticalTrapezoid tHead = null;


    for(int i = 0; i < size; i++) {
      Vertex v = orderedVertices[i];
      List<VerticalTrapezoid> leftSideTrapezoids = new ArrayList<VerticalTrapezoid>();
      List<VerticalTrapezoid> rightSideTrapezoids = new ArrayList<VerticalTrapezoid>();
      ProxyVerticalTrapezoidVertex pv = new ProxyVerticalTrapezoidVertex(v);

      //Wenn der Vertex in einem Trapez liegt
      System.out.println("ATT SIZE: " + att.size());
      if(att.contains(pv)) {
        VerticalTrapezoid trapezoid = att.floor(pv).toTrapezoid();
        leftSideTrapezoids.add(trapezoid);

        if(vertexIsLeftTrap(outline, v)) {
          // Add another Trapezoid;
        }
      }

      rightSideTrapezoids = createTrapezoids(leftSideTrapezoids, v, outline);
      if(tHead == null) tHead = rightSideTrapezoids.get(0);

      for(VerticalTrapezoid trapezoid : leftSideTrapezoids) {
        System.out.println("ATT SIZE / BEFORE REMOVE: " + att.size());
        att.remove(new ProxyVerticalTrapezoidVertex(trapezoid));
        System.out.println("ATT SIZE / AFTER REMOVE: " + att.size());
      }
      for(VerticalTrapezoid trapezoid : rightSideTrapezoids) {
        System.out.println("ATT SIZE / BEFORE ADD: " + att.size());
        att.add(new ProxyVerticalTrapezoidVertex(trapezoid));
      }
    }
    drawTrapezoids(tHead, null);
  }

  public void drawTrapezoids(VerticalTrapezoid t, VerticalTrapezoid previous) {
    drawTrapezoid(t);
    for(VerticalTrapezoid _t : t.getNext()) {
      if(previous == null) System.out.println("prev null");
      if(!_t.equals(previous))
        drawTrapezoids(_t, t);
    }
    for(VerticalTrapezoid _t : t.getPrev()) {
      if(!_t.equals(previous))
        drawTrapezoids(_t, t);
    }
  }

  public void drawTrapezoid(VerticalTrapezoid t) {
    System.out.println(t);
    System.out.println("Drawing");
    double x;


    Line l1 = new Line(t.left.start.x, t.left.start.y, t.left.end.x, t.left.end.y);
    Line l2 = new Line(t.right.start.x, t.right.start.y, t.right.end.x, t.right.end.y);
    Line l3 = new Line(t.top.start.x, t.top.start.y, t.top.end.x, t.top.end.y);
    Line l4 = new Line(t.bot.start.x, t.bot.start.y, t.bot.end.x, t.bot.end.y);
    l1.setStroke(Color.color(1,0,0,0.3));
    l2.setStroke(Color.color(1,0,0,0.3));
    l3.setStroke(Color.color(1,0,0,0.3));
    l4.setStroke(Color.color(1,0,0,0.3));
    textLayer.getChildren().add(l1);
    textLayer.getChildren().add(l2);
    textLayer.getChildren().add(l3);
    textLayer.getChildren().add(l4);
  }

  public void drawLineSegment(LineSegment l) {
    Line l1 = new Line(l.start.x, l.start.y, l.end.x, l.end.y);
    l1.setStroke(Color.color(1,0,0,0.3));
    textLayer.getChildren().add(l1);
  }

  /**
   * TODO Die Überprüfung kann theoretisch auch ein Start für einen neuen Punkt sein.
   * @method VertexIsRightTrap
   * @param  outline           [description]
   * @param  v                 [description]
   * @return                   [description]
   */
  public boolean vertexIsRightTrap(VertexList outline, Vertex v) {
    Vertex next = outline.getNext(v);
    Vertex prev = outline.getPrev(v);
    if(next.x > v.x && prev.x > v.x) return true;

    return false;
  }

  public boolean vertexIsLeftTrap(VertexList outline, Vertex v) {
    return false;
  }

  public Vertex[] intersection(VerticalTrapezoid trapezoid, Vertex v) {
    LineSegment vertical = new LineSegment(v.x, 0, v.x, 1000);
    Vertex intersectionTop = new Vertex(-1, -1);
    Vertex intersectionBot = new Vertex(-1, -1);
    vertical.getLineIntersection(trapezoid.top, intersectionTop);
    vertical.getLineIntersection(trapezoid.bot, intersectionBot);

    return new Vertex[] {intersectionTop, intersectionBot};
  }

  public List<VerticalTrapezoid> createTrapezoids(List<VerticalTrapezoid> leftSideTrapezoids, Vertex v, VertexList outline) {
    System.out.println("CREATING");
    List<VerticalTrapezoid> rightSideTrapezoids = new ArrayList<VerticalTrapezoid>();

    Vertex next = outline.getNext(v);
    Vertex prev = outline.getPrev(v);
    LineSegment vNext = new LineSegment(v, next);
    LineSegment vPrev = new LineSegment(v, prev);
    Vertex nextTop = vNext.slope() < vPrev.slope() ? next : prev;
    Vertex nextBot = vNext.slope() > vPrev.slope() ? next : prev;
    Vertex nextRight = next.x > v.x ? next : (prev.x > v.x ? prev : null);
    Vertex nextLeft = next.x < v.x ? next : (prev.x < v.x ? prev : null);

    if(leftSideTrapezoids.size() == 1) {
      System.out.println("FALL 1");
      VerticalTrapezoid trapezoid = leftSideTrapezoids.get(0);
      Vertex[] intersections = intersection(trapezoid, v);
      VirtualVertex vTop;
      VirtualVertex vBot;

      if(!intersections[0].equals(v) && !intersections[0].equals(new Vertex(-1, -1))) {
        vTop = new VirtualVertex(intersections[0]);
      } else {
        vTop = null;
      }
      if(!intersections[1].equals(v) && !intersections[1].equals(new Vertex(-1, -1))) {
        vBot = new VirtualVertex(intersections[1]);
      } else {
        vBot = null;
      }
      // if(!intersections[0].equals(new Vertex(-1,-1))) {
      //   vTop = new VirtualVertex(intersections[0]);
      // } else {
      //   vTop = null;
      // }
      // if(!intersections[1].equals(new Vertex(-1,-1))) {
      //   vBot = new VirtualVertex(intersections[1]);
      // } else {
      //   vBot = null;
      // }

      if(vertexIsRightTrap(outline, v)) {
        System.out.println("FALL 2");

        //Merge 2 Polygons into 1
        /**
         * 1
         * Wenn es in einem der Fälle keinen Schnittpunkt gibt,
         * dann liegen die Punkte auf der gleichen X-Koordinate
         * Allgemeine Lage verhindert das.
         * ABER TODO
         */
        if(vBot == null || vTop == null) {
          System.out.println("Allgemeine Lage nicht eingehalten");
        }
        VerticalTrapezoid topTrap = new VerticalTrapezoid(vTop, v);
        VerticalTrapezoid botTrap = new VerticalTrapezoid(v, vBot);
        rightSideTrapezoids.add(topTrap);
        rightSideTrapezoids.add(botTrap);

        trapezoid.right = new LineSegment(vTop, vBot);
        topTrap.top = trapezoid.top;
        topTrap.bot = new LineSegment(v, nextTop);

        botTrap.top = new LineSegment(v, nextBot);
        botTrap.bot = trapezoid.bot;
        trapezoid.top = new LineSegment(trapezoid.left.start, vTop);
        trapezoid.bot = new LineSegment(trapezoid.left.end, vBot);

        topTrap.addPreviousTrapezoid(trapezoid);
        botTrap.addPreviousTrapezoid(trapezoid);
        trapezoid.addNextTrapezoid(topTrap);
        trapezoid.addNextTrapezoid(botTrap);

      } else {
        System.out.println("FALL 3");

        //Attach 1 Polygon to another
        //Fall Eckpunkt oder Punkte liegen übereinander

        if(vBot == null && vTop == null) {
          //Fall Eckpunkt
          if(trapezoid.top.end.equals(trapezoid.bot.end)) {
            trapezoid.right = new LineSegment(v,v);
            trapezoid.top = new LineSegment(trapezoid.left.start, v);
            trapezoid.bot = new LineSegment(trapezoid.left.end, v);
          } else if(trapezoid.top.end.equals(v)) {
            trapezoid.right = new LineSegment(v, trapezoid.bot.end);
            trapezoid.top = new LineSegment(trapezoid.left.start, v);
            trapezoid.bot = new LineSegment(trapezoid.left.end, v);
          }
          //Previous schon vorhanden. Next = Null
        } else if(trapezoid.top.end.equals(v)) {
          System.out.println("FALL 4");

          trapezoid.right = new LineSegment(v, vBot);
          VerticalTrapezoid newTrapezoid = new VerticalTrapezoid(trapezoid.right);
          newTrapezoid.top = new LineSegment(v, nextRight);
          newTrapezoid.bot = trapezoid.bot;
          trapezoid.top = new LineSegment(trapezoid.left.start, v);
          trapezoid.bot = new LineSegment(trapezoid.left.end, vBot);
          trapezoid.addNextTrapezoid(newTrapezoid);
          newTrapezoid.addPreviousTrapezoid(trapezoid);
          rightSideTrapezoids.add(newTrapezoid);
        } else {
          System.out.println("FALL 5");

          trapezoid.right = new LineSegment(vTop, v);
          VerticalTrapezoid newTrapezoid = new VerticalTrapezoid(trapezoid.right);
          newTrapezoid.bot = new LineSegment(v, nextRight);
          newTrapezoid.top = trapezoid.top;
          trapezoid.top = new LineSegment(trapezoid.left.start, vTop);
          trapezoid.bot = new LineSegment(trapezoid.left.end, v);
          trapezoid.addNextTrapezoid(newTrapezoid);
          newTrapezoid.addPreviousTrapezoid(trapezoid);
          rightSideTrapezoids.add(newTrapezoid);
        }
      }
    } else if(leftSideTrapezoids.size() == 0) {
      //Erstelle neues Polygon
      VerticalTrapezoid newTrapezoid = null;
      //Top Left StartVertex
      if(v.x == next.x && v.y < next.y || v.x == prev.x && v.y < prev.y) {
        Vertex leftVertex = next.y > v.y ? next : prev;
        Vertex rightTop = next.y > v.y ? prev : next;
        Vertex rightBot = outline.getNext(v).equals(v) ? outline.getPrev(v) : outline.getNext(v);
        newTrapezoid = new VerticalTrapezoid(new LineSegment(v, leftVertex));
        newTrapezoid.bot = new LineSegment(leftVertex, rightBot);
        newTrapezoid.top = new LineSegment(v, rightTop);
      } else {
        newTrapezoid = new VerticalTrapezoid(new LineSegment(v,v));
        newTrapezoid.top = new LineSegment(v,nextTop);
        newTrapezoid.bot = new LineSegment(v,nextBot);
      }
      rightSideTrapezoids.add(newTrapezoid);
    } else {
      //Fall Left Trap

    }
    return rightSideTrapezoids;
  }
}
