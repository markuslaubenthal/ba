import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;

interface TextStrategy {
  public void drawText(VertexPolygon poly, Pane textLayer);
}
