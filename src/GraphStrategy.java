import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.io.FileInputStream;
import javafx.geometry.Bounds;

class GraphStrategy implements TextStrategy{

  public GraphStrategy(){
  }

  public void drawText(VertexPolygon poly, Pane textLayer){
    try {

    double[] bb = poly.getBoundingBox();
    double minSize = (bb[1]-bb[0]) / (2 * poly.getText().length());
    int density = 4;
    Graph g = new Graph(poly, minSize, density);
    g.generateNetwork();
    ArrayList<GraphVertex> path = g.findLongestPath();

    int verteciesPerLetter = path.size() / poly.getText().length();
    int verteciesleft = path.size() % poly.getText().length();

    for(int i = 0; i < poly.getText().length(); i++) {

      GraphVertex centerVertex = new GraphVertex(0,0);
      centerVertex.setScore(0);

      int badVerticies = 0;
      for(int j = 0; j < verteciesPerLetter; j++) {
        int index = verteciesleft / 2 + i * verteciesPerLetter + j;
        centerVertex.x += path.get(index).x;
        centerVertex.y += path.get(index).y;
        if(path.get(index).score < 0) badVerticies++;
        centerVertex.score += Math.max(path.get(index).score , 0);
      }

      centerVertex.x = centerVertex.x / verteciesPerLetter;
      centerVertex.y = centerVertex.y / verteciesPerLetter;
      centerVertex.score = centerVertex.score / (verteciesPerLetter - badVerticies);

      double fontsize = verteciesPerLetter * minSize / density;

      Font monospacedFont;
      Text t = new Text();
      monospacedFont = Font.font("Courier New", FontWeight.NORMAL, fontsize);
      if(monospacedFont.getFamily() != "Courier New") {
        monospacedFont = Font.font("Courier New", FontWeight.NORMAL, fontsize);
      }
      t.setFont(monospacedFont);
      t.setText(poly.getText().substring(i, i + 1).toUpperCase());
      Bounds b = t.getLayoutBounds();
      double boundingBot = b.getMaxY();
      double boundingTop = b.getMinY();
      double ascent = Math.abs(boundingTop);
      double descent = Math.abs(boundingBot);
      double middle = Math.abs(boundingTop + boundingBot) / 2;

      double difference = Math.abs(Math.abs(middle) - Math.abs(middle - ascent));
      t.setX(centerVertex.x - (fontsize / 2));
      double padding = 0.9;
      double scale = ((centerVertex.score + density - 1) * minSize / density / (ascent - middle)) * padding;
      t.setY(centerVertex.y + middle);
      t.setScaleY(scale);
      t.setScaleX(1.5);
      textLayer.getChildren().add(t);
    }
  } catch (Exception e){
    System.out.println("nothing");
  }

  }

}
