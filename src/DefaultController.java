import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.io.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javafx.scene.control.TextField;
import java.util.Random;

class DefaultController {

  ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();

  Boolean creatingNewPolygon = false;
  VertexPolygon newPolygon;
  VertexPolygon currentPolyToEdit = new VertexPolygon();
  DefaultView view;

  double fontsize;

  public DefaultController(DefaultView view) {
    this.view = view;
  }

  public void addVertex(double x, double y) {
    Vertex v = new Vertex(x, y);
    addVertexPoint(v);
  }

  public void addVertexPoint(Vertex vertex) {
    if(creatingNewPolygon) {
      Circle point = new Circle();
      vertex.point = point;
      double x = vertex.x;
      double y = vertex.y;

      // Hole das neueste Polygon aus der Liste.
      newPolygon.addVertex(vertex);

      point.setCenterX(x);
      point.setCenterY(y);
      point.setRadius(6.0);
      point.setFill(Color.LAWNGREEN);

      point.setOnMouseDragged(new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          view.dropBackground();
          double deltaX = Math.abs(point.getCenterX() - event.getSceneX());
          double deltaY = Math.abs(point.getCenterY() - event.getSceneY());
          if(deltaX + deltaY > 2){
            point.setCenterX(event.getSceneX());
            point.setCenterY(event.getSceneY());
            vertex.x = event.getSceneX();
            vertex.y = event.getSceneY();
            view.drawPolygons(polygonList);
          }
        }
      });
      point.setOnMousePressed(new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          if(creatingNewPolygon && !newPolygon.contains(vertex)){
            newPolygon.addVertex(vertex);
            point.setFill(Color.LAWNGREEN);
          }
        }
      });
      view.drawPoint(point);
    }
  }

  public void handlePreviousButton() {
    int prevIndex = polygonList.lastIndexOf(currentPolyToEdit) - 1;
    if(prevIndex >= 0) {
      currentPolyToEdit = polygonList.get(prevIndex);
      TextField polygonTextField = view.getPolygonTextField();
      polygonTextField.setText(currentPolyToEdit.getText());
    }
  }

  public void handleNextButton() {
    int nextIndex = polygonList.lastIndexOf(currentPolyToEdit) + 1;
    if(nextIndex < polygonList.size()){
      currentPolyToEdit = polygonList.get(nextIndex);
      TextField polygonTextField = view.getPolygonTextField();
      polygonTextField.setText(currentPolyToEdit.getText());
    }
  }

  public void handleUpdateButton() {
    TextField polygonTextField = view.getPolygonTextField();
    currentPolyToEdit.setText(polygonTextField.getText());
    view.drawPolygons(polygonList);
  }

  public void handleNewButton() {
    if(!creatingNewPolygon) {
      creatingNewPolygon = true;
      newPolygon = new VertexPolygon(String.valueOf(polygonList.size()));
      Button newPolyBtn = view.getNewPolyButton();
      newPolyBtn.setText("End Polygon");
    } else {
      polygonList.add(newPolygon);
      newPolygon.colorizeVertecies(0,0,0,1);
      view.drawPolygons(polygonList);
      creatingNewPolygon = false;
      currentPolyToEdit = newPolygon;
      TextField polygonTextField = view.getPolygonTextField();
      polygonTextField.setText(newPolygon.getText());
      Button newPolyBtn = view.getNewPolyButton();
      newPolyBtn.setText("New Polygon");
    }
  }

  public void handleSaveButton() {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showSaveDialog(null);
    if(file != null) {
      PolygonWriter writer = new PolygonWriter(polygonList);
      writer.save(file);
    }
  }

  public void handleLoadButton() {
    FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(null);
    if(file != null) {
      PolygonReader reader = new PolygonReader(file);
      ArrayList<VertexPolygon> _polygonList = reader.get();

      ArrayList<Vertex> _vertexList = new ArrayList<Vertex>();

      polygonList = new ArrayList<VertexPolygon>();
      view.clearVertices();
      for(VertexPolygon polygon : _polygonList) {
        creatingNewPolygon = false;
        handleNewButton();
        for(Vertex v : polygon.getOutline()) {
          if(!_vertexList.contains(v)) {
            addVertexPoint(v);
            _vertexList.add(v);
          } else {
            v = _vertexList.get(_vertexList.indexOf(v));
            newPolygon.addVertex(v);
          }
        }
        handleNewButton();
      }

      // view.drawPolygons(polygonList);
    }
  }

  public void handleScanButton() {
    VertexPolygon example = polygonList.get(0);
    scanPolygons();
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

  public void scanPolygons() {
    view.dropBackground();
    for(VertexPolygon p : polygonList) {
      // p.setText("WESENTLICH MEHR BEISPIELTEXT");
      ArrayList<LineSegment> lines = scanPolygon(p);
      createRectanglesFromLineSegments(lines, p);
    }
  }

  public ArrayList<LineSegment> scanPolygon(VertexPolygon polygon) {
    fontsize = calculateFontSize(polygon);
    double rate = fontsize / 8;
    Random r = new Random();
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
          view.drawVertex(intersection);
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


        view.drawText(t);
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

  double calculateUsableSpace(ArrayList<LineSegment> lines, double minHeight, double minWidth) {
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

  int findFirstUsableLineSegment(ArrayList<LineSegment> lines, int offset, double minHeight) {
    for(int i = offset; i < lines.size(); i++) {
      if(lines.get(i).getHeight() >= minHeight) return i;
    }
    return lines.size() - 1;
  }

  int findLastUsableLineSegment(ArrayList<LineSegment> lines, int offset, double minHeight) {
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
