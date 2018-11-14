import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.text.*;

class SimpleStrategy implements TextStrategy{

  public SimpleStrategy(){
  }

  public void drawText(VertexPolygon poly, Pane textLayer){
    ArrayList<Vertex> outline = poly.outline;
    String text = poly.text;
    double leftest = 1000;
    double rightest = 0;
    double highest = 1000;
    double lowest = 0;
    for(Vertex v : outline){
      leftest = Math.min(leftest, v.x);
      rightest = Math.max(rightest, v.x);
      highest = Math.min(highest, v.y);
      lowest = Math.max(lowest, v.y);
    }
    double voulume = (rightest - leftest) * (lowest - highest);
    int fontsize = (int) (Math.sqrt(voulume/text.length()) * 0.7);

    double x = leftest + (rightest - leftest) * (0.3 / 2);
    double y = highest + fontsize + (lowest - highest) * (0.3 / 2);

    Text t = new Text();
    t.setFont(new Font(fontsize));
    t.setText(text);
    t.setWrappingWidth((rightest-leftest) * 0.7);
    t.setTextAlignment(TextAlignment.JUSTIFY);
    t.setX(x);
    t.setY(y);
    textLayer.getChildren().add(t);
  }

}
