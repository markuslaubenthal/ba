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


public class Gui {
  HBox layer0 = new HBox();
  VBox layer1UI = new VBox();
  Pane layer1CS = new Pane();
  Pane layer2CS = new Pane();
  Pane layer3CS = new Pane();
  Pane layer4CS = new Pane();
  Scene scene = new Scene(layer0, 1300, 800);

  List<Vertex> vertexList = new ArrayList<>();
  Boolean newPolygon = false;

  public Gui(Stage stage) {
    stage.setScene(scene);
    addButtons();
    setupKlickToAddVertex();
    layer0.getChildren().add(layer1CS);
    layer0.getChildren().add(layer1UI);
    layer1CS.getChildren().add(layer2CS);
    layer2CS.getChildren().add(layer3CS);
    layer3CS.getChildren().add(layer4CS);
  }

  public void addVertex(double x, double y){
    Vertex vertex = new Vertex();
    Circle point = new Circle();
    point.setCenterX(x);
    point.setCenterY(y);
    point.setRadius(6.0);

    point.setOnMouseDragged(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
        double deltaX = Math.abs(point.getCenterX() - event.getSceneX());
        double deltaY = Math.abs(point.getCenterY() - event.getSceneY());
        if(deltaX + deltaY > 2){
          point.setCenterX(event.getSceneX());
          point.setCenterY(event.getSceneY());
          //vertexList.get(position).x = event.getSceneX();
          //vertexList.get(position).y = event.getSceneY();
        }
      }
    });
    point.setOnMousePressed(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
        // IF WE ARE CREATING A NEW POLYGON
        // ADD TO POLYGON LIST
        point.setFill(Color.LAWNGREEN);
      }
    });
    layer3CS.getChildren().add(point);
  }

  public void setupKlickToAddVertex(){
    Rectangle r = new Rectangle();
    r.setX(0);
    r.setY(0);
    r.setWidth(1000);
    r.setHeight(800);
    r.setFill(Color.color(0,0,0,0));
    r.setOnMousePressed(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
        addVertex(event.getX(), event.getY());
      }
    });
    layer4CS.getChildren().add(r);
  }
  public void addButtons(){
    Button btn = new Button("New Polygon");
    btn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        if(!newPolygon) {
          btn.setText("End Polygon");
          newPolygon = true;
        } else {
          btn.setText("New Polygon");
          newPolygon = false;
        }
      }
    });
    layer1UI.getChildren().add(btn);
  }

}
