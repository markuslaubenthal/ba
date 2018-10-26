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


public class Gui {
  int windowHeight = 800;
  int windowWidth =  1300;
  int vertexBoxHeight = 800;
  int vertexBoxWidth = 1000;
  HBox rootContainer = new HBox();
  VBox uiContainer = new VBox();

  Pane vertexLayer = new Pane();
  Pane edgeLayer = new Pane();
  Pane emptyLayer = new Pane();

  Scene scene = new Scene(rootContainer, windowWidth, windowHeight);

  List<VertexPolygon> polygonList = new ArrayList<>();

  Boolean creatingNewPolygon = false;
  VertexPolygon newPolygon;
  VertexPolygon currentPolyToEdit = new VertexPolygon();

  public Gui(Stage stage) {
    stage.setScene(scene);
    addUserInteraction();
    setupKlickToAddVertex();
    rootContainer.getChildren().add(emptyLayer);
    rootContainer.getChildren().add(uiContainer);
    emptyLayer.getChildren().add(edgeLayer);
    emptyLayer.getChildren().add(vertexLayer);
  }

  public void addVertex(double x, double y){
    if(creatingNewPolygon){
      Circle point = new Circle();
      Vertex vertex = new Vertex(x, y, point);

      // Hole das neueste Polygon aus der Liste.
      newPolygon.addVertex(vertex);

      point.setCenterX(x);
      point.setCenterY(y);
      point.setRadius(6.0);
      point.setFill(Color.LAWNGREEN);

      point.setOnMouseDragged(new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          double deltaX = Math.abs(point.getCenterX() - event.getSceneX());
          double deltaY = Math.abs(point.getCenterY() - event.getSceneY());
          if(deltaX + deltaY > 2){
            point.setCenterX(event.getSceneX());
            point.setCenterY(event.getSceneY());
            vertex.x = event.getSceneX();
            vertex.y = event.getSceneY();
            drawPolygons();
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
      vertexLayer.getChildren().add(point);
    }
  }

  public void setupKlickToAddVertex(){
    Rectangle r = new Rectangle();
    r.setX(0);
    r.setY(0);
    r.setWidth(vertexBoxWidth);
    r.setHeight(vertexBoxHeight);
    r.setFill(Color.color(0,0,0,0));
    r.setOnMousePressed(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
        addVertex(event.getX(), event.getY());
      }
    });
    vertexLayer.getChildren().add(r);
  }

  public void addUserInteraction(){

    TextField polygonTextField = new TextField();
    uiContainer.getChildren().add(polygonTextField);

    HBox navigationContainer = new HBox();
    uiContainer.getChildren().add(navigationContainer);

    Button prevBtn = new Button("prev");
    Button updateBtn = new Button("update");
    Button nextBtn = new Button("next");

    prevBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        int prevIndex = polygonList.lastIndexOf(currentPolyToEdit) - 1;
        if(prevIndex >= 0){
          currentPolyToEdit = polygonList.get(prevIndex);
          polygonTextField.setText(currentPolyToEdit.getText());
        }
      }
    });
    updateBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        currentPolyToEdit.setText(polygonTextField.getText());
        drawPolygons();
      }
    });
    nextBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        int nextIndex = polygonList.lastIndexOf(currentPolyToEdit) + 1;
        if(nextIndex < polygonList.size()){
          currentPolyToEdit = polygonList.get(nextIndex);
          polygonTextField.setText(currentPolyToEdit.getText());
        }
      }
    });


    navigationContainer.getChildren().add(prevBtn);
    navigationContainer.getChildren().add(updateBtn);
    navigationContainer.getChildren().add(nextBtn);


    Button newPolyBtn = new Button("New Polygon");
    newPolyBtn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        if(!creatingNewPolygon) {
          creatingNewPolygon = true;
          newPolygon = new VertexPolygon(String.valueOf(polygonList.size()));
          newPolyBtn.setText("End Polygon");
        } else {
          polygonList.add(newPolygon);
          newPolygon.colorizeVertecies(0,0,0,1);
          drawPolygons();
          creatingNewPolygon = false;
          currentPolyToEdit = newPolygon;
          polygonTextField.setText(newPolygon.getText());
          newPolyBtn.setText("New Polygon");
        }
      }
    });
    uiContainer.getChildren().add(newPolyBtn);
  }

  public void drawPolygons(){
    edgeLayer.getChildren().clear();
    for(VertexPolygon poly : polygonList) {
      poly.drawPolygon(edgeLayer);
      poly.drawText(edgeLayer);
    }
  }

}
