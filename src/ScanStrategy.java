import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import java.util.Collections;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.io.FileInputStream;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Bounds;

class ScanStrategy implements TextStrategy{
  double fontsize;
  Pane textLayer;


  public ScanStrategy(){
    System.out.println("Scan Strategy Constructor");
  }

  public void drawText(VertexPolygon poly, Pane textLayer){
    this.textLayer = textLayer;
    ArrayList<LineSegment> lines = scanPolygon(poly);
    try{
      createRectanglesFromLineSegments(lines, poly);
    } catch (Exception e){
      System.out.println(e);
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
          intersectionList.add(intersection);
        }
        current = next;
      }
      Collections.sort(intersectionList, new VertexYComparator());
      for(int j = 0; j < intersectionList.size() - 1; j+=2) {
        lineSegmentList.add(new LineSegment(intersectionList.get(j), intersectionList.get(j+1)));
      }
    }

    return lineSegmentList;
  }

  public void createRectanglesFromLineSegments(ArrayList<LineSegment> lines, VertexPolygon p) {
    int letter = 0;
    double minHeight = fontsize * 0.7;
    double minWidth = (fontsize * 0.7);
    double usableSpace = calculateUsableSpace(lines, minHeight, minWidth);
    double rectangleWidth = usableSpace / p.getText().length() * 0.9;

    int endOffset = 0;
    int startOffset = findFirstUsableLineSegment(lines, 0, minHeight);

    while(startOffset < lines.size()) {
      LineSegment start = lines.get(startOffset);
      LineSegment end = null;
      double top = start.end.y;
      double bot = start.start.y;
      if(start.getHeight() >= minHeight) {
        for(endOffset = startOffset + 1; endOffset < lines.size(); endOffset++) {
          LineSegment tmp = lines.get(endOffset);
          if(tmp.getHeight() < minHeight) {
            startOffset = endOffset + 1;
          } else {
            double space = tmp.start.x - start.start.x;
            top = Math.min(top, tmp.end.y);
            bot = Math.max(bot, tmp.start.y);
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
        Font monospacedFont;
        Text t = new Text();
        monospacedFont = Font.font("Courier New", FontWeight.NORMAL, minWidth);
        if(monospacedFont.getFamily() != "Courier New") {
          monospacedFont = Font.font("Courier New", FontWeight.NORMAL, minWidth);
        }


        String myLetter = p.getText().substring(letter, letter + 1);
        double baseline = top;

        t.setFont(monospacedFont);
        t.setText(p.getText().substring(letter, letter + 1));
        Bounds b = t.getLayoutBounds();
        double boundingBot = b.getMaxY();
        double boundingTop = b.getMinY();
        double ascent = Math.abs(boundingTop);
        double descent = Math.abs(boundingBot);
        double middle = Math.abs(boundingTop + boundingBot) / 2;

        t.setY(baseline);

        System.out.println("Letter: " + myLetter);
        System.out.println("Ascent:  " + ascent);
        System.out.println("Descent: " + descent);
        System.out.println("Middle: " + middle);
        System.out.println("----------");
        System.out.println("");
        t.setX(start.start.x);
        t.setScaleX(1.5);

        double requiredHeight = Math.abs(top - bot);
        double actualHeight = ascent;

        double scale = requiredHeight / actualHeight;
        t.setScaleY(scale * 2);
        t.setTranslateY( - middle * (scale - 1));
        textLayer.getChildren().add(t);

        letter++;
      } else {
        startOffset = lines.size();
      }


    }

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

      double space = end.start.x - start.start.x;
      if(space >= minWidth)
        usableSpace += space;
      startOffset = findFirstUsableLineSegment(lines, endOffset + 1, minHeight);
    }

    return usableSpace;


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

}
