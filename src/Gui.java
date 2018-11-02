import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

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
