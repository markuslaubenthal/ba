import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
      scanPolygon(p);
    }
  }

  public ArrayList<LineSegment> scanPolygon(VertexPolygon polygon) {
    double rate = 13;
    Random r = new Random();
    rate *= 1.0f + (2.0f - 1.0f) * r.nextDouble();

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
          // System.out.println("Intersection: " + intersection.y);
          view.drawVertex(intersection);
          intersectionList.add(intersection);
        }
        current = next;
      }
      Collections.sort(intersectionList, new VertexYComparator());
      for(int j = 0; j < intersectionList.size() - 1; j+=2) {
        view.drawLine(intersectionList.get(j), intersectionList.get(j+1));
      }
    }

    return new ArrayList<LineSegment>();
  }

}
