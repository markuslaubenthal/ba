import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.io.FileInputStream;
import javafx.geometry.Bounds;

class GraphStrategy implements TextStrategy{

  double factor = 0.9;

  public GraphStrategy(){
  }
  public GraphStrategy(double factor){
    this.factor = factor;
  }

  public void drawText(VertexPolygon originalPoly, Pane textLayer){
    try {

    VertexPolygon poly = Geometry.scalePolygon(originalPoly, factor);


    //double[] bb = poly.getBoundingBox();
    //double minSize = (bb[1]-bb[0]) / (2 * poly.getText().length());
    double minSize = Math.sqrt(poly.getAreaSize()) / poly.getText().length();
    int density = Math.max(4, 4 * 4 / poly.getText().length());
    Graph g = new Graph(poly, minSize, density);
    g.generateNetwork();
    ArrayList<GraphVertex> initialPath = g.findLongestPath();

    if(initialPath == null) throw new NullPointerException("no vertecies");

    ArrayList<GraphVertex> path = new ArrayList<GraphVertex>();

    for(GraphVertex p : initialPath) {
      if(p.getScore() > 0) path.add(p);
    }

    int verteciesPerLetter = path.size() / poly.getText().length();
    int verteciesleft = path.size() % poly.getText().length();

    for(int i = 0; i < poly.getText().length(); i++) {

      GraphVertex centerVertex = new GraphVertex(0,0);
      centerVertex.setScore(0);

      for(int j = 0; j < verteciesPerLetter; j++) {
        int index = verteciesleft / 2 + i * verteciesPerLetter + j;
        centerVertex.x += path.get(index).x;
        centerVertex.y += path.get(index).y;
        centerVertex.score += path.get(index).score;
      }

      centerVertex.x = centerVertex.x / verteciesPerLetter;
      centerVertex.y = centerVertex.y / verteciesPerLetter;
      centerVertex.score = centerVertex.score / verteciesPerLetter;

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
    } catch (Exception e) {
      System.out.println(e);
    }
  }


}
