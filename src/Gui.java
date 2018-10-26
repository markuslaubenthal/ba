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

  List<VertexPolygon> polygonListe = new ArrayList<>();

  Boolean creatingNewPolygon = false;
  VertexPolygon newPolygon;

  public Gui(Stage stage) {
    stage.setScene(scene);
    addButtons();
    setupKlickToAddVertex();
    layer0.getChildren().add(layer1CS);
    layer0.getChildren().add(layer1UI);
    layer1CS.getChildren().add(layer2CS);
    layer1CS.getChildren().add(layer3CS);
    layer1CS.getChildren().add(layer4CS);
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
      layer4CS.getChildren().add(point);
    }
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
        if(!creatingNewPolygon) {
          creatingNewPolygon = true;
          newPolygon = new VertexPolygon(String.valueOf(polygonListe.size()));
          btn.setText("End Polygon");
        } else {
          polygonListe.add(newPolygon);
          newPolygon.colorizeVertecies(0,0,0,1);
          drawPolygons();
          creatingNewPolygon = false;
          btn.setText("New Polygon");
        }
      }
    });
    layer1UI.getChildren().add(btn);
  }

  public void drawPolygons(){
    layer3CS.getChildren().clear();
    for(VertexPolygon poly : polygonListe) {
      poly.drawPolygon(layer3CS);
      poly.drawText(layer3CS);
    }
  }

}
