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
  HBox rootContainer = new HBox();
  Scene scene = new Scene(rootContainer, windowWidth, windowHeight);



  public Gui(Stage stage) {
    stage.setScene(scene);
    DefaultView defaultView = new DefaultView(rootContainer);
  }

}
