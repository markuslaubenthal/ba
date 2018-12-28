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

  DefaultControllerState state = new DefaultControllerState();
  ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();

  VertexPolygon newPolygon;
  DefaultView view;

  public DefaultController(DefaultView view) {
    this.view = view;
  }

  public void addVertexAndPoint(double x, double y) {
    if(state.isBuildingNewPolygon()) {
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
    if(state.isBuildingNewPolygon() && !newPolygon.contains(vertex)) {
      newPolygon.addVertex(vertex);
      point.setFill(Color.LAWNGREEN);
    }
  }

  public void handlePreviousButton() {
    int prevIndex = polygonList.lastIndexOf(state.getSelectedPolygon()) - 1;
    if(prevIndex >= 0) {
      selectPolygon(polygonList.get(prevIndex));
    }
  }

  public void handleNextButton() {
    int nextIndex = polygonList.lastIndexOf(state.getSelectedPolygon()) + 1;
    if(nextIndex < polygonList.size()){
      selectPolygon(polygonList.get(nextIndex));
    }
  }

  public void handleUpdateButton() {
    TextField polygonTextField = view.getPolygonTextField();
    state.getSelectedPolygon().setText(polygonTextField.getText());
    view.refresh();
  }

  public void handleNewButton() {
    if(!state.isBuildingNewPolygon()) {
      state.startBuildingNewPolygon();
      newPolygon = new VertexPolygon(String.valueOf(polygonList.size())+String.valueOf(polygonList.size())+String.valueOf(polygonList.size())+String.valueOf(polygonList.size()));
      newPolygon.setTextStrategy(StrategyFactory.getStrategy(StrategyFactory.Default));
      view.getNewPolyButton().setText("End Polygon");
    } else {
      polygonList.add(newPolygon);
      view.refresh();
      selectPolygon(newPolygon);
      view.getNewPolyButton().setText("New Polygon");
      state.stopBuildingNewPolygon();
    }
  }

  public void selectPolygon(VertexPolygon poly) {
    state.setSelectedPolygon(poly);
    view.getPolygonTextField().setText(poly.getText());
    view.getStrategyCombobox().setValue(StrategyFactory.getName(poly.getStrategy()));
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
      for(int i = 0; i < polygonList.size(); i++) {
        polygonList.get(i).setTextStrategy(StrategyFactory.getStrategy(StrategyFactory.Default));
      }
      newPolygon = null;
      state.stopBuildingNewPolygon();
      view.refresh();
      selectPolygon(polygonList.get(0));
    }
  }

  public void handleOpacityButton() {
    double opacity = state.togglePolygonOpacity() ? 1.0 : 0.0;
    view.setEdgeLayerOpacity(opacity);
    view.setMainLayerOpacity(opacity);
  }

  public void handleStrategyDropDown() {
    String strategy = (String) view.getStrategyCombobox().getValue();
    state.getSelectedPolygon().setTextStrategy(StrategyFactory.getStrategy(strategy));
    //view.refresh();
  }
}
