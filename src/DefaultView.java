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
import javafx.scene.control.TextField;


class DefaultView {
  int drawingAreaHeight = 800;
  int drawingAreaWidth = 1000;

  Pane root;

  VBox uiContainer = new VBox();

  Pane vertexLayer = new Pane();
  Pane edgeLayer = new Pane();
  Pane emptyLayer = new Pane();

  TextField polygonTextField;
  Button newPolyBtn;

  DefaultController controller;

  public DefaultView(Pane root) {
    controller = new DefaultController(this);
    root.getChildren().add(emptyLayer);
    root.getChildren().add(uiContainer);
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

    navigationContainer.getChildren().add(prevBtn);
    navigationContainer.getChildren().add(updateBtn);
    navigationContainer.getChildren().add(nextBtn);
    uiContainer.getChildren().add(newPolyBtn);
  }

  public void drawPolygons(ArrayList<VertexPolygon> polygonList) {
    edgeLayer.getChildren().clear();
    for(VertexPolygon poly : polygonList) {
      poly.drawPolygon(edgeLayer);
      poly.drawText(edgeLayer);
    }
  }

  public void drawPoint(Circle point) {
    vertexLayer.getChildren().add(point);
  }

  public TextField getPolygonTextField() {
    return polygonTextField;
  }

  public Button getNewPolyButton() {
    return newPolyBtn;
  }


}
