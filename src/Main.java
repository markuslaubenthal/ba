import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Startet und initialisiert die GUI
 *
 *  java -cp packages/java-json.jar:target Test
 *  startet die Anwendung
 *
 */

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage stage){
    stage.setTitle("Text Placement in Polygons");
    Gui userInterface = new Gui(stage);
    stage.show();
  }
}
