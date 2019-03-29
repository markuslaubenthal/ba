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
      List<VerticalTrapezoid> trapezoids = getTrapezoidalDecomposition(outline, orderedVertices);
      drawTrapezoids(trapezoids.get(0), null);
      lineBreak(outline, 5);
    } catch (Exception e){
      e.printStackTrace(new java.io.PrintStream(System.out));
    }
  }

  public Vertex[] sort(VertexList hashset) {
    Vertex[] v = (Vertex[])hashset.toArray();
    Arrays.sort(v, new VertexXComparator());
    return v;
  }


  public List<VerticalTrapezoid> getTrapezoidalDecomposition(VertexList outline, Vertex[] orderedVertices) {

    int size = orderedVertices.length;
    //Active Trapezoid Tree
    TreeSet<ProxyVerticalTrapezoidVertex> att = new TreeSet<ProxyVerticalTrapezoidVertex>(new VerticalTrapezoidVertexComparator());
    VerticalTrapezoid tHead = null;
    List<VerticalTrapezoid> trapezoidList= new LinkedList<VerticalTrapezoid>();


    for(int i = 0; i < size; i++) {
      Vertex v = orderedVertices[i];
      List<VerticalTrapezoid> leftSideTrapezoids = new ArrayList<VerticalTrapezoid>();
      List<VerticalTrapezoid> rightSideTrapezoids = new ArrayList<VerticalTrapezoid>();
      ProxyVerticalTrapezoidVertex pv = new ProxyVerticalTrapezoidVertex(v);

      //Wenn der Vertex in einem Trapez liegt
      if(att.contains(pv)) {
        ProxyVerticalTrapezoidVertex pt = att.floor(pv);
        VerticalTrapezoid trapezoid = pt.toTrapezoid();
        leftSideTrapezoids.add(trapezoid);

        if(vertexIsLeftTrap(outline, v) && hasIntersection(trapezoid, v)) {
          System.out.println("-----------------------LEFT TRAP");
          ProxyVerticalTrapezoidVertex pt1 = att.higher(pt);
          ProxyVerticalTrapezoidVertex pt2 = att.lower(pt);
          if(pt1 == null) {
            leftSideTrapezoids.add(pt2.toTrapezoid());
          } else if(pt2 == null) {
            leftSideTrapezoids.add(pt1.toTrapezoid());
          } else if(pt1.toTrapezoid().contains(v)) {
            leftSideTrapezoids.add(pt1.toTrapezoid());
          } else if(pt2.toTrapezoid().contains(v)) {
            leftSideTrapezoids.add(pt2.toTrapezoid());
          } else {
            System.out.println("KEIN TRAPEZ");
          }
        }
      }

      rightSideTrapezoids = createTrapezoids(leftSideTrapezoids, v, outline);
      if(tHead == null) tHead = rightSideTrapezoids.get(0);

      for(VerticalTrapezoid trapezoid : leftSideTrapezoids) {
        att.remove(new ProxyVerticalTrapezoidVertex(trapezoid));
      }
      for(VerticalTrapezoid trapezoid : rightSideTrapezoids) {
        if(!(trapezoid instanceof VerticalTrapezoidFiller)) {
          att.add(new ProxyVerticalTrapezoidVertex(trapezoid));
        }
        //Sortiere nach Anfang des Polygons
        trapezoidList.add(trapezoid);
      }
    }
    // drawTrapezoids(tHead, null);
    return trapezoidList;
  }

  public void drawTrapezoids(VerticalTrapezoid t, VerticalTrapezoid previous) {
    drawTrapezoid(t);
    for(VerticalTrapezoid _t : t.getNext()) {
      if(!_t.equals(previous))
        drawTrapezoids(_t, t);
    }
    for(VerticalTrapezoid _t : t.getPrev()) {
      if(!_t.equals(previous))
        drawTrapezoids(_t, t);
    }
  }

  public void drawTrapezoid(VerticalTrapezoid t) {
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
    Vertex next = outline.getNext(v);
    Vertex prev = outline.getPrev(v);
    if(next.x < v.x && prev.x < v.x) return true;

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

  public boolean hasIntersection(VerticalTrapezoid trapezoid, Vertex v) {
    Vertex[] intersections = intersection(trapezoid, v);
    if(!intersections[0].equals(v) && !intersections[0].equals(new Vertex(-1, -1))) {
      return true;
    }
    if(!intersections[1].equals(v) && !intersections[1].equals(new Vertex(-1, -1))) {
      return true;
    }
    return false;
  }

  public List<VerticalTrapezoid> createTrapezoids(List<VerticalTrapezoid> leftSideTrapezoids, Vertex v, VertexList outline) {
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
          VerticalTrapezoid endTrap = new VerticalTrapezoidFiller(trapezoid.right);
          endTrap.top = new LineSegment(trapezoid.right.start, trapezoid.right.start);
          endTrap.bot = new LineSegment(trapezoid.right.start, trapezoid.right.start);
          endTrap.right = trapezoid.right;
          trapezoid.addNextTrapezoid(endTrap);
          endTrap.addPreviousTrapezoid(trapezoid);
          rightSideTrapezoids.add(endTrap);
          System.out.println("ENDPUNKT");
          System.out.println(endTrap.area());
          //Previous schon vorhanden. Next = Null
        } else if(trapezoid.top.end.equals(v)) {

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
      //Fall Left Trap / leftside_1 = top, leftside_2 = bot
      VerticalTrapezoid trapTop = leftSideTrapezoids.get(0);
      VerticalTrapezoid trapBot = leftSideTrapezoids.get(1);
      if(trapTop.left.start.y > trapBot.left.start.y) {
        VerticalTrapezoid tmp = trapTop;
        trapTop = trapBot;
        trapBot = tmp;
      }

      Vertex[] intersections = intersection(trapTop, v);
      VirtualVertex vTop = null;
      VirtualVertex vBot = null;
      System.out.println(v);
      System.out.println("HALLO");
      if(!intersections[0].equals(v) && !intersections[0].equals(new Vertex(-1, -1))) {
        vTop = new VirtualVertex(intersections[0]);
        System.out.println(vTop);
      }
      // if(!intersections[1].equals(v) && !intersections[1].equals(new Vertex(-1, -1))) {
      //   vBot = new VirtualVertex(intersections[1]);
      // }

      intersections = intersection(trapBot, v);
      // if(!intersections[0].equals(v) && !intersections[0].equals(new Vertex(-1, -1))) {
      //   vTop = new VirtualVertex(intersections[0]);
      // }
      if(!intersections[1].equals(v) && !intersections[1].equals(new Vertex(-1, -1))) {
        vBot = new VirtualVertex(intersections[1]);
        System.out.println(vBot);
      }

      VerticalTrapezoid newTrapezoid = new VerticalTrapezoid(new LineSegment(vTop, vBot));
      newTrapezoid.top = trapTop.top;
      newTrapezoid.bot = trapBot.bot;
      trapTop.right = new LineSegment(vTop, v);
      trapTop.top = new LineSegment(trapTop.left.start, vTop);
      trapTop.bot = new LineSegment(trapTop.left.end, v);
      trapBot.right = new LineSegment(v, vBot);
      trapBot.top = new LineSegment(trapBot.left.start, v);
      trapBot.bot = new LineSegment(trapBot.left.end, vBot);
      trapTop.addNextTrapezoid(newTrapezoid);
      trapBot.addNextTrapezoid(newTrapezoid);
      newTrapezoid.addPreviousTrapezoid(trapTop);
      newTrapezoid.addPreviousTrapezoid(trapBot);
      System.out.println(vTop);
      System.out.println(v);
      System.out.println(vBot);
      double aslkdjalsd = newTrapezoid.left.start.y;
      rightSideTrapezoids.add(newTrapezoid);
    }
    return rightSideTrapezoids;
  }

  // public double[] calculateUniformAreasize(List<VerticalTrapezoid> trapList, int lineCount) {
  //   double area = 0;
  //   for(VerticalTrapezoid trapezoid : trapList) {
  //     area += trapezoid.area();
  //   }
  //   return area;
  // }

  public void lineBreak(VertexList outline, int lineCount) {
    rotateCounterClockwise(outline);
    //Linked List mit konstanter Einfüge Operation
    Vertex[] orderedVertices = sort(outline);
    LinkedList<VerticalTrapezoid> trapList = (LinkedList<VerticalTrapezoid>)getTrapezoidalDecomposition(outline, orderedVertices);
    double areaPolygon = area(trapList);
    HashSet<VerticalTrapezoid> ats = new HashSet<VerticalTrapezoid>();
    // double area[] = new double[trapList.size()];
    double area = 0;
    double area_last = 0;
    double sweep = 0;
    double a1 = 0;
    double a2 = 0;
    double b1 = 0;
    double b2 = 0;
    double s_last = -9999;
    double counter = 1;

    List<LineSegment> breakingLines = new LinkedList<LineSegment>();

    for(VerticalTrapezoid trapezoid : trapList) {
      System.out.println("PRINT AREA");
      System.out.println(trapezoid.area());
      sweep = trapezoid.left.start.x;
      if(s_last == -9999) {
        s_last = sweep;
      }

      // area += a1/2 * Math.pow(sweep,2) + b1*sweep - (a1/2 * Math.pow(s_last,2) + b1 * s_last)
      //   - (a2/2 * Math.pow(sweep,2) + b2 * sweep - (a2/2 * Math.pow(s_last,2) + b2 * s_last));
      area += a2/2 * Math.pow(sweep,2) + b2*sweep - (a2/2 * Math.pow(s_last,2) + b2 * s_last)
        - (a1/2 * Math.pow(sweep,2) + b1 * sweep - (a1/2 * Math.pow(s_last,2) + b1 * s_last));
      System.out.println("AREA: " + area);
      System.out.println("POLYGON: " + areaPolygon);
      while(area > areaPolygon * (counter/(lineCount + 1)) && counter <= lineCount) {
        //Trapez gefunden
        double Fleft = areaPolygon * counter / (lineCount + 1) - area_last;
        // double x = -((b1 - b2 + Math.sqrt(Math.pow((b1-b2),2) + (2*(b1*s_last-b2*s_last+Fleft)+a1*Math.pow(s_last,2)-a2*Math.pow(s_last,2))*(a1-a2)))/(a1-a2));
        double x = -((b2 - b1 - Math.sqrt(Math.pow((b2-b1),2) + (2*(b2*s_last-b1*s_last+Fleft)+a2*Math.pow(s_last,2)-a1*Math.pow(s_last,2))*(a2-a1)))/(a2-a1));
        LineSegment vLine = new LineSegment(new Vertex(x, 0), new Vertex(x, 1000));
        vLine.start.rotateClockwise();
        vLine.end.rotateClockwise();
        breakingLines.add(vLine);
        drawLineSegment(vLine);
        counter++;
      }


      //Erst rechnen, dann hinzufügen

      a1 += trapezoid.top.slope();
      a2 += trapezoid.bot.slope();
      b1 += trapezoid.top.functionOffset();
      b2 += trapezoid.bot.functionOffset();
      // ats.add(trapezoid);
      for(VerticalTrapezoid t : trapezoid.getPrev()) {
        a1 -= t.top.slope();
        a2 -= t.bot.slope();
        b1 -= t.top.functionOffset();
        b2 -= t.bot.functionOffset();
        // ats.remove(t);
      }
      s_last = sweep;
      area_last = area;
    }



    rotateClockwise(outline);
  }

  public void rotateCounterClockwise(VertexList outline) {
    Vertex v = null;
    for(int i = 0; i < outline.size(); i++) {
      v = outline.next(v);
      v.rotateCounterClockwise();
      // v.x += 800;
    }
  }

  public void rotateClockwise(VertexList outline) {
    Vertex v = null;
    for(int i = 0; i < outline.size(); i++) {
      v = outline.next(v);
      // v.x -= 800;
      v.rotateClockwise();
    }
  }

  public double area(List<VerticalTrapezoid> trapezoids) {
    double area = 0;
    for(VerticalTrapezoid t : trapezoids) {
      area += t.area();
    }
    return area;
  }

}
