import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import java.util.Collections;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;


class ScanStrategy implements TextStrategy{
  double fontsize;
  Pane textLayer;

  public ScanStrategy(){

  }

  public void drawText(VertexPolygon poly, Pane textLayer){
    this.textLayer = textLayer;
    ArrayList<LineSegment> lines = scanPolygon(poly);
    try{
      createRectanglesFromLineSegments(lines, poly);
    } catch (Exception e){
      System.out.println("catch all exception DEAL WITH IT");
    }
  }

  public double findLeftest(VertexPolygon p) {
    double leftest = 1000;
    for(Vertex v : p.getOutline()) {
      if(v.x < leftest) leftest = v.x;
    }
    return leftest;
  }

  public double findRightest(VertexPolygon p) {
    double rightest = 0;
    for(Vertex v : p.getOutline()) {
      if(v.x > rightest) rightest = v.x;
    }
    return rightest;
  }

  public ArrayList<LineSegment> scanPolygon(VertexPolygon polygon) {
    fontsize = calculateFontSize(polygon);
    double rate = fontsize / 8;
    //Random r = new Random();
    // rate *= 1.0f + (2.0f - 1.0f) * r.nextDouble();

    ArrayList<LineSegment> lineSegmentList = new ArrayList<LineSegment>();

    for(double x = findLeftest(polygon); x < findRightest(polygon); x += rate) {
      LineSegment line = new LineSegment(x, 0, x, 1000);
      ArrayList<Vertex> outline = polygon.getOutline();
      Vertex current = outline.get(0);
      Vertex next = null;

      ArrayList<Vertex> intersectionList = new ArrayList<Vertex>();
      for(int i = 1; i < outline.size() + 1; i++) {
        if(i == outline.size()) {
          next = outline.get(0);
        } else {
          next = outline.get(i);
        }

        LineSegment l = new LineSegment(current, next);

        Vertex intersection = new Vertex(0, 0);
        if(line.getLineIntersection(l, intersection)) {
          // drawVertex(intersection);
          intersectionList.add(intersection);
        }
        current = next;
      }
      Collections.sort(intersectionList, new VertexYComparator());
      for(int j = 0; j < intersectionList.size() - 1; j+=2) {
        lineSegmentList.add(new LineSegment(intersectionList.get(j), intersectionList.get(j+1)));
        // view.drawLine(intersectionList.get(j), intersectionList.get(j+1));
      }
    }

    return lineSegmentList;
  }

  public void createRectanglesFromLineSegments(ArrayList<LineSegment> lines, VertexPolygon p) {
    int letter = 0;
    double minHeight = fontsize * 0.7;
    double minWidth = fontsize * 0.7;
    double usableSpace = calculateUsableSpace(lines, minHeight, minWidth);
    double rectangleWidth = usableSpace / p.getText().length() * 0.9;

    // LineSegment current = lines.get(0);
    // LineSegment next = lines.get(0);
    int endOffset = 0;
    int startOffset = findFirstUsableLineSegment(lines, 0, minHeight);
    while(startOffset < lines.size()) {
      // endOffset = findLastUsableLineSegment(lines, startOffset + 1, minHeight);
      LineSegment start = lines.get(startOffset);
      LineSegment end = null;

      if(start.getHeight() >= minHeight) {
        for(endOffset = startOffset + 1; endOffset < lines.size(); endOffset++) {
          LineSegment tmp = lines.get(endOffset);
          if(tmp.getHeight() < minHeight) {
            startOffset = endOffset + 1;
          } else {
            double space = tmp.start.x - start.start.x;
            if(space >= rectangleWidth) {
              startOffset = endOffset;
              end = lines.get(endOffset);
              break;
            }
          }
        }
      } else {
        startOffset++;
      }

      if(end != null) {
        double top = Math.min(start.end.y, end.end.y);
        double bot = Math.max(start.start.y, end.start.y);
        // view.drawRectangle(start.start.x, start.start.x + rectangleWidth, top, bot);

        Text t = new Text();
        t.setFont(new Font(minWidth));
        t.setText(p.getText().substring(letter, letter + 1));
        t.setX(start.start.x);
        t.setY(top);
        t.setScaleX(1);
        double scale = Math.abs(top - bot) / minWidth;
        t.setScaleY(scale);
        t.setTranslateY(-(minWidth * (scale - 1) / 2));


        drawText(t);
        letter++;

      } else {
        startOffset = lines.size();
      }


      // LineSegment start = lines.get(startOffset);
      // LineSegment end = lines.get(endOffset);
      // System.out.println(space);
      // if(space >= 1) {
      //   double top = Math.min(start.end.y, end.end.y);
      //   double bot = Math.max(start.start.y, end.start.y);
      //   view.drawRectangle(start.start.x, end.start.x, top, bot);
      // }
      // startOffset = findFirstUsableLineSegment(lines, endOffset + 1, minHeight);
    }

    // for(int i = 1; i < lines.size(); i++) {
    //   LineSegment tmp = lines.get(i);
    //   // if(tmp.start.x > next.start.x) {
    //     next = tmp;
    //     double top = Math.min(current.end.y, next.end.y);
    //     double bot = Math.max(current.start.y, next.start.y);
    //     view.drawRectangle(current.start.x, next.start.x, top, bot);
    //     current = next;
    //   // }
    // }
  }

  public double calculateUsableSpace(ArrayList<LineSegment> lines, double minHeight, double minWidth) {
    int startOffset = 0;
    int endOffset = 0;
    double width;

    double usableSpace = 0;

    startOffset = findFirstUsableLineSegment(lines, 0, minHeight);
    while(startOffset != lines.size() - 1) {
      endOffset = findLastUsableLineSegment(lines, startOffset + 1, minHeight);

      LineSegment start = lines.get(startOffset);
      LineSegment end = lines.get(endOffset);

      // int space = (int) (((double) (end.start.x - start.start.x)) / minWidth);
      double space = end.start.x - start.start.x;
      if(space >= minWidth)
        usableSpace += space;
      startOffset = findFirstUsableLineSegment(lines, endOffset + 1, minHeight);
    }

    return usableSpace;


    // LineSegment current = lines.get(0);
    // for(int i = 1; i < lines.size(); i++) {
    //   LineSegment next = lines.get(i);
    //   height = Math.abs(line.start.y - line.end.y);
    //   width = Math.abs(line.start.x - next.start.x);
    //   if(height >= minHeight) {
    //     if(offset == 0) offset += 1;
    //   }
    // }
  }

  public int findFirstUsableLineSegment(ArrayList<LineSegment> lines, int offset, double minHeight) {
    for(int i = offset; i < lines.size(); i++) {
      if(lines.get(i).getHeight() >= minHeight) return i;
    }
    return lines.size() - 1;
  }

  public int findLastUsableLineSegment(ArrayList<LineSegment> lines, int offset, double minHeight) {
    for(int i = offset; i < lines.size(); i++) {
      if(lines.get(i).getHeight() < minHeight) return i - 1;
    }
    return lines.size() - 1;
  }

  public double calculateFontSize(VertexPolygon p) {
    double leftest = 1000;
    double rightest = 0;
    double highest = 1000;
    double lowest = 0;
    for(Vertex v : p.getOutline()){
      leftest = Math.min(leftest, v.x);
      rightest = Math.max(rightest, v.x);
      highest = Math.min(highest, v.y);
      lowest = Math.max(lowest, v.y);
    }
    double voulume = (rightest - leftest) * (lowest - highest);
    return ((rightest - leftest)/p.getText().length());
  }

  public void drawVertex(Vertex v) {
    Circle point = new Circle();
    point.setCenterX(v.x);
    point.setCenterY(v.y);
    point.setRadius(2.0);
    point.setFill(Color.RED);
    textLayer.getChildren().add(point);
  }

  public void drawLine(Vertex s, Vertex t) {
    Line line = new Line();
    line.setStartX(s.x);
    line.setStartY(s.y);
    line.setEndX(t.x);
    line.setEndY(t.y);
    line.setStrokeWidth(1);
    textLayer.getChildren().add(line);
  }

  public void drawRectangle(double left, double right, double top, double bottom) {
    Rectangle r = new Rectangle();
    r.setX(left);
    r.setY(bottom);
    r.setWidth(Math.abs(right - left));
    r.setHeight(Math.abs(top - bottom));
    r.setFill(Color.RED);
    textLayer.getChildren().add(r);
  }

  public void dropBackground() {
    textLayer.getChildren().clear();
  }


  public void drawText(Text t) {
    textLayer.getChildren().add(t);
  }

}
