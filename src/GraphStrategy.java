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
<<<<<<< HEAD

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

=======
    int verteciesPerLetter = (cleanPath.size() - density * 3) / poly.getText().length();
    for(int i = 0; i < poly.getText().length(); i++) {
      GraphVertex centerVertexL = cleanPath.get((int) density * 2 + i*verteciesPerLetter + verteciesPerLetter / 2);
      GraphVertex centerVertexR = cleanPath.get((int) density * 2 + i*verteciesPerLetter + (verteciesPerLetter + 1) / 2);
      GraphVertex centerVertex = new GraphVertex((centerVertexL.x + centerVertexR.x) / 2, (centerVertexL.y + centerVertexR.y) / 2);
      centerVertex.setScore(Math.min(centerVertexL.getScore(), centerVertexR.getScore()));
>>>>>>> font-tests
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

<<<<<<< HEAD
      double difference = Math.abs(Math.abs(middle) - Math.abs(middle - ascent));
      t.setX(centerVertex.x - (fontsize / 2));
      double padding = 0.9;
      double scale = ((centerVertex.score + density - 1) * minSize / density / (ascent - middle)) * padding;
      t.setY(centerVertex.y + middle);
      t.setScaleY(scale);
=======
      LineSegment test = new LineSegment(centerVertex.sub(new Vertex(0, 1000)), centerVertex.add(new Vertex(0, 1000)));
      ArrayList<Vertex> intersections = new ArrayList<Vertex>();
      for(int index = 0; index < poly.getOutline().size();index++) {
        Vertex intersection = new Vertex(0,0);
        if(test.getLineIntersection(poly.getLineSegment(index), intersection)) {
          intersections.add(intersection);
        }
      }
      VertexYComparator.sort(intersections);
      Vertex cv = new Vertex(centerVertex.x,0);
      for(int index = 0; index < intersections.size(); index++) {
        if(intersections.get(index).y < centerVertex.y && intersections.get(index + 1).y > centerVertex.y) {
          double midpoint = (intersections.get(index).y + intersections.get(index+1).y) / 2;
          cv.y = midpoint;
        }
      }

      double difference = Math.abs(Math.abs(middle) - Math.abs(middle - ascent));
      t.setX(cv.x - (fontsize / 2));
      double padding = 1;
      double scale = ((centerVertex.score + density - 1.0) * minSize / density / (ascent - middle));
      t.setY(cv.y + middle + descent / 2.0);
      t.setScaleY(scale);
      System.out.println(descent);
      // t.setTranslateY((middle - descent) * (scale - 1));
      // t.setTranslateY((middle - descent / 2) * (scale - 1));
      // t.setTranslateY(Math.abs(ascent - middle) - middle * (scale - 1));
>>>>>>> font-tests
      t.setScaleX(1.5);
      textLayer.getChildren().add(t);
    }
    } catch (Exception e) {
      System.out.println(e);
    }
  }


}
