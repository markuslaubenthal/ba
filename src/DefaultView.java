import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
import javafx.scene.control.TextField;
import javafx.scene.text.*;


class DefaultView {
  int drawingAreaHeight = 800;
  int drawingAreaWidth = 1000;

  Pane root;

  VBox uiContainer = new VBox();

  Pane vertexLayer = new Pane();
  Pane edgeLayer = new Pane();
  Pane backgroundLayer = new Pane();
  Pane emptyLayer = new Pane();

  TextField polygonTextField;
  Button newPolyBtn;

  DefaultController controller;

  public DefaultView(Pane root) {
    controller = new DefaultController(this);
    root.getChildren().add(emptyLayer);
    root.getChildren().add(uiContainer);
    emptyLayer.getChildren().add(backgroundLayer);
    emptyLayer.getChildren().add(edgeLayer);
    emptyLayer.getChildren().add(vertexLayer);
    addOnClickActionListenerOnDrawingArea();
    addUserInteraction();
  }

  public void addOnClickActionListenerOnDrawingArea() {
    Rectangle r = new Rectangle();
    r.setX(0);
    r.setY(0);
    r.setWidth(drawingAreaWidth);
    r.setHeight(drawingAreaHeight);
    r.setFill(Color.color(0,0,0,0));
    r.setOnMousePressed(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
        controller.addVertex(event.getX(), event.getY());
      }
    });
    vertexLayer.getChildren().add(r);
  }

  public void addUserInteraction(){
    polygonTextField = new TextField();
    uiContainer.getChildren().add(polygonTextField);

    HBox navigationContainer = new HBox();
    uiContainer.getChildren().add(navigationContainer);

    Button prevBtn = new Button("prev");
    Button updateBtn = new Button("update");
    Button nextBtn = new Button("next");
    newPolyBtn = new Button("New Polygon");
    Button scanBtn = new Button("Scan");

    prevBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handlePreviousButton();
      }
    });
    updateBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleUpdateButton();
      }
    });
    nextBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleNextButton();
      }
    });
    newPolyBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleNewButton();
      }
    });
    scanBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleScanButton();
      }
    });
    Button saveButton = new Button("Save file...");
    saveButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleSaveButton();
      }
    });

    Button loadButton = new Button("Load file...");
    loadButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        controller.handleLoadButton();
      }
    });

    navigationContainer.getChildren().add(prevBtn);
    navigationContainer.getChildren().add(updateBtn);
    navigationContainer.getChildren().add(nextBtn);
    uiContainer.getChildren().add(newPolyBtn);
    uiContainer.getChildren().add(loadButton);
    uiContainer.getChildren().add(saveButton);
    uiContainer.getChildren().add(scanBtn);
  }

  public void drawPolygons(ArrayList<VertexPolygon> polygonList) {
    edgeLayer.getChildren().clear();
    for(VertexPolygon poly : polygonList) {
      poly.drawPolygon(edgeLayer);
      poly.drawText(edgeLayer);
    }
    controller.scanPolygons();
  }

  public void clearVertices() {
    vertexLayer.getChildren().clear();
    addOnClickActionListenerOnDrawingArea();
  }

  public void drawPoint(Circle point) {
    vertexLayer.getChildren().add(point);
  }

  public void drawVertex(Vertex v) {
    Circle point = new Circle();
    point.setCenterX(v.x);
    point.setCenterY(v.y);
    point.setRadius(2.0);
    point.setFill(Color.RED);
    backgroundLayer.getChildren().add(point);
  }

  public void drawLine(Vertex s, Vertex t) {
    Line line = new Line();
    line.setStartX(s.x);
    line.setStartY(s.y);
    line.setEndX(t.x);
    line.setEndY(t.y);
    line.setStrokeWidth(1);
    backgroundLayer.getChildren().add(line);
  }

  public void drawRectangle(double left, double right, double top, double bottom) {
    Rectangle r = new Rectangle();
    r.setX(left);
    r.setY(bottom);
    r.setWidth(Math.abs(right - left));
    r.setHeight(Math.abs(top - bottom));
    r.setFill(Color.RED);
    backgroundLayer.getChildren().add(r);
  }

  public void dropBackground() {
    backgroundLayer.getChildren().clear();
  }

  public TextField getPolygonTextField() {
    return polygonTextField;
  }

  public void drawText(Text t) {
    backgroundLayer.getChildren().add(t);
  }

  public Button getNewPolyButton() {
    return newPolyBtn;
  }

}
