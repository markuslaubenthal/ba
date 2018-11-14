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


  public DefaultController(DefaultView view) {
    this.view = view;
  }

  public void addVertexAndPoint(double x, double y) {
    if(creatingNewPolygon){
      Vertex v = new Vertex(x, y);
      view.addPointToVertex(v);
      v.point.setFill(Color.LAWNGREEN);
      newPolygon.addVertex(v);
    }
  }

  public void handleMouseDragged(MouseEvent event, Circle point, Vertex vertex){
    double deltaX = Math.abs(point.getCenterX() - event.getSceneX());
    double deltaY = Math.abs(point.getCenterY() - event.getSceneY());
    if(deltaX + deltaY > 2){
      point.setCenterX(event.getSceneX());
      point.setCenterY(event.getSceneY());
      vertex.x = event.getSceneX();
      vertex.y = event.getSceneY();
      vertex.dragged = true;
      view.refreshDrag();
    }
  }

  public void handleMousePressed(Circle point, Vertex vertex){
    if(creatingNewPolygon && !newPolygon.contains(vertex)){
      newPolygon.addVertex(vertex);
      point.setFill(Color.LAWNGREEN);
    }
  }

  public void handlePreviousButton() {
    int prevIndex = polygonList.lastIndexOf(currentPolyToEdit) - 1;
    if(prevIndex >= 0) {
      updateTextfield(polygonList.get(prevIndex));
    }
  }

  public void handleNextButton() {
    int nextIndex = polygonList.lastIndexOf(currentPolyToEdit) + 1;
    if(nextIndex < polygonList.size()){
      updateTextfield(polygonList.get(nextIndex));
    }
  }

  public void handleUpdateButton() {
    TextField polygonTextField = view.getPolygonTextField();
    currentPolyToEdit.setText(polygonTextField.getText());
    view.refresh();
  }

  public void handleNewButton() {
    if(!creatingNewPolygon) {
      creatingNewPolygon = true;
      newPolygon = new VertexPolygon(String.valueOf(polygonList.size())+String.valueOf(polygonList.size())+String.valueOf(polygonList.size())+String.valueOf(polygonList.size()));
      view.getNewPolyButton().setText("End Polygon");
    } else {
      polygonList.add(newPolygon);
      view.refresh();
      updateTextfield(newPolygon);
      view.getNewPolyButton().setText("New Polygon");
      creatingNewPolygon = false;
    }
  }

  public void updateTextfield(VertexPolygon poly){
    currentPolyToEdit = poly;
    TextField polygonTextField = view.getPolygonTextField();
    polygonTextField.setText(poly.getText());
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
      polygonList = reader.get();
      newPolygon = null;
      creatingNewPolygon = false;
      view.refresh();
      updateTextfield(polygonList.get(0));
    }
  }
}
