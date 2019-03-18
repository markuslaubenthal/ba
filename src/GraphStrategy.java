import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.io.FileInputStream;
import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

class GraphStrategy implements TextStrategy{

  double factor = 1;

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
    //double minSize = Math.sqrt(poly.getAreaSize()) / poly.getText().length();
    int density = Math.max(8, 8 * 4 / poly.getText().length());
    // int density = Math.max(10, 50 / poly.getText().length());

    double minSize = Math.sqrt((poly.getAreaSize() / poly.getText().length()) / 3) ;

    Graph g = new Graph(poly, minSize, density);
    g.generateNetwork();
    ArrayList<GraphVertex> path = g.findLongestPath();

    if(path == null) throw new NullPointerException("no vertecies");

    // ArrayList<GraphVertex> path = new ArrayList<GraphVertex>();
    //
    // for(GraphVertex p : initialPath) {
    //   if(p.getScore() > 0) path.add(p);
    // }

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

      if(centerVertex.score < density) centerVertex.score = density / 2;

      double fontsize = verteciesPerLetter * minSize / density;

      Font monospacedFont;
      Text t = new Text();
      monospacedFont = new Font("Cartograph Mono CF Heavy", fontsize);
      // if(monospacedFont.getFamily() != "Cartograph Mono CF") {
      //   monospacedFont = Font.font("Cartograph Mono CF", FontWeight.THIN, fontsize);
      // }
      String letterI = poly.getText().substring(i, i + 1).toUpperCase();
      t.setFont(monospacedFont);
      t.setText(letterI);
      t.setBoundsType(TextBoundsType.VISUAL);

      Bounds b = t.getLayoutBounds();
      double height = b.getHeight();
      double width = b.getWidth();
      double boundingLeft = b.getMinX();
      double boundingBot = b.getMaxY();

      t.setX(centerVertex.x - width / 2 - boundingLeft);
      t.setScaleX(1.7);
      t.setY(centerVertex.y + height / 2 - boundingBot);
      if(!letterI.equals("-")) t.setScaleY(((centerVertex.score + density - 1) * minSize) / (density * height));




      textLayer.getChildren().add(t);


    }
    } catch (Exception e) {
      System.out.println(e);
    }
  }


}
