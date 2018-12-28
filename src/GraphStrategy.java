import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.text.*;
import javafx.scene.paint.Color;

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
    ArrayList<GraphVertex> path = g.findLongestPathGreedy();
    ArrayList<GraphVertex> cleanPath = new ArrayList<GraphVertex>();

    for(GraphVertex v : path) {
      if(v.getScore() > 0) {
        cleanPath.add(v);
      }
    }
    int verteciesPerLetter = (cleanPath.size() - density * 2) / poly.getText().length();
    for(int i = 0; i < poly.getText().length(); i++) {
      GraphVertex centerVertexL = cleanPath.get((int) density + i*verteciesPerLetter + verteciesPerLetter / 2);
      GraphVertex centerVertexR = cleanPath.get((int) density + i*verteciesPerLetter + (verteciesPerLetter + 1) / 2);
      GraphVertex centerVertex = new GraphVertex((centerVertexL.x + centerVertexR.x) / 2, (centerVertexL.y + centerVertexR.y) / 2);
      centerVertex.setScore(Math.min(centerVertexL.getScore(), centerVertexR.getScore()));
      double fontsize = verteciesPerLetter * minSize / density;

      Text t = new Text();
      Font monospacedFont = Font.font("Courier New", FontWeight.NORMAL, fontsize);
      t.setFont(monospacedFont);
      t.setText(poly.getText().substring(i, i + 1).toUpperCase());
      t.setX(centerVertex.x - (fontsize / 2));
      t.setY(centerVertex.y + (fontsize / 2));
      t.setScaleY((centerVertex.score + density - 1) * minSize / density / fontsize);
      t.setScaleX(1.5);
      textLayer.getChildren().add(t);
    }
  } catch (Exception e){
    System.out.println("nothing");
  }

  }

}
