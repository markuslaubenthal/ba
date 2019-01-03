import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

class GraphSplitStrategy implements TextStrategy {

  VertexPolygon poly;
  Pane textLayer;

  public GraphSplitStrategy() {}

  public void drawText(VertexPolygon originalPoly, Pane textLayer) {

    VertexPolygon poly = Geometry.scalePolygon(originalPoly, 0.8);

    ArrayList<VertexPolygon> subPolygonList = new ArrayList<VertexPolygon>();

    /*
    Split the text into two parts based on word seperations like space or dash,
    hyphenation or by simply splitting the text in the center.
     */

    String[] textParts = splitText(poly.text);

    /*
    Create subpolygons with the area ratio matching the ratio of the two text parts.
     */

    double upperRatio = (double)textParts[0].length() / poly.text.length();
    double lowerRatio = (double)textParts[1].length() / poly.text.length();

    VertexPolygon[] polygonParts = Geometry.findSplitLineApprox(poly, upperRatio, lowerRatio);

    //  <<<<<<<<<<<<<<<<<< BEGIN DEBUG <<<<<<<<<<<<<<<<<<<<<<<<
    /*

    System.out.println("<<<<<<<<<<<<<");
    System.out.println(poly.text);
    System.out.println(upperRatio);
    System.out.println(polygonParts[0].getAreaSize() / poly.getAreaSize());
    System.out.println(lowerRatio);
    System.out.println(polygonParts[1].getAreaSize() / poly.getAreaSize());

    Polygon drawPoly = new Polygon();

    for(Vertex v : polygonParts[0].getOutline()) {
      drawPoly.getPoints().addAll(new Double[] {v.x, v.y} );
    }
    drawPoly.setFill(Color.color(0,0,0,0));
    drawPoly.setStrokeWidth(2);
    drawPoly.setStroke(Color.BLACK);
    textLayer.getChildren().add(drawPoly);

    drawPoly = new Polygon();

    for(Vertex v : polygonParts[1].getOutline()) {
      drawPoly.getPoints().addAll(new Double[] {v.x, v.y} );
    }
    drawPoly.setFill(Color.color(0,0,0,0));
    drawPoly.setStrokeWidth(2);
    drawPoly.setStroke(Color.BLACK);
    textLayer.getChildren().add(drawPoly);
    */

    //  <<<<<<<<<<<<<<<<<< END DEBUG <<<<<<<<<<<<<<<<<<<<<<<<

    polygonParts[0].text = textParts[0];
    polygonParts[1].text = textParts[1];
    subPolygonList.add(polygonParts[0]);
    subPolygonList.add(polygonParts[1]);


    /*

    int density = 4;
    int minVerteciesPerLetter = verteciesPerLetter(poly, density);

    ArrayList<VertexPolygon> subPolygonList = new ArrayList<VertexPolygon>();
    ArrayList<Integer> subPolygonVPLList = new ArrayList<Integer>();
    subPolygonList.add(poly);
    subPolygonVPLList.add(minVerteciesPerLetter);

    int zz = 0;
    while(minVerteciesPerLetter < density && zz < 3) {
      zz++;

      ArrayList<VertexPolygon> newSubPolygonList = new ArrayList<VertexPolygon>();
      ArrayList<Integer> newSubPolygonVPLList = new ArrayList<Integer>();

      for(int i = 0; i < subPolygonList.size(); i++) {
        if(subPolygonVPLList.get(i) < density) {
          String[] textParts = splitText(subPolygonList.get(i).text);
          VertexPolygon[] polygonParts = Geometry.splitPolygonOnBestBottleneck(subPolygonList.get(i));
          if(polygonParts.length < 2 || textParts.length < 2){
            newSubPolygonList.add(subPolygonList.get(i));
            newSubPolygonVPLList.add(subPolygonVPLList.get(i) + 1);
            System.out.println("hallo");
            continue;
          }
          polygonParts[0].text = textParts[0];
          polygonParts[1].text = textParts[1];
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);
          int verteciesPerLetter0 = verteciesPerLetter(polygonParts[0], density);
          int verteciesPerLetter1 = verteciesPerLetter(polygonParts[1], density);
          newSubPolygonVPLList.add(verteciesPerLetter0);
          newSubPolygonVPLList.add(verteciesPerLetter1);
          if(verteciesPerLetter0 < minVerteciesPerLetter) minVerteciesPerLetter = verteciesPerLetter0;
          if(verteciesPerLetter1 < minVerteciesPerLetter) minVerteciesPerLetter = verteciesPerLetter1;
        } else {
          newSubPolygonList.add(subPolygonList.get(i));
          newSubPolygonVPLList.add(subPolygonVPLList.get(i));
        }
      }

      subPolygonList = newSubPolygonList;
      subPolygonVPLList = newSubPolygonVPLList;

    }
    */

    for(VertexPolygon p : subPolygonList) {
      p.setTextStrategy(new GraphStrategy());
      p.drawText(textLayer);
      p.setTextStrategy(this);
    }

  }

  private int verteciesPerLetter(VertexPolygon poly, int density) {
    double minSize = Math.sqrt(poly.getAreaSize()) / poly.getText().length();
    Graph g = new Graph(poly, minSize, density);
    g.generateNetwork();
    ArrayList<GraphVertex> path = g.findLongestPathGreedy();
    ArrayList<GraphVertex> cleanPath = new ArrayList<GraphVertex>();

    for(GraphVertex v : path) {
      if(v.getScore() > 0) {
        cleanPath.add(v);
      }
    }

    return (cleanPath.size() - density * 2) / poly.getText().length();

  }

  private String[] splitText(String text) {

    if(text.contains(" ") || text.contains("-")){

      /*
      loop through text to find positions of spaces and dashes
      wich naturally seperate the text.
      Retruns the two substrings with the most center seperator.
       */

      ArrayList<Integer> indexList = new ArrayList<Integer>();

      for (int i = 0; i < text.length(); i++){

        char c = text.charAt(i);

        if(c == ' ' || c == '-') {

          indexList.add(i);

        }

      }

      int mid = indexList.get(indexList.size()/2);

      /*
      If we seperate at a dash we want to keep the dash with the first substring.
      If we find a space we will not add it to either substring since they are allready seperated now.
       */

      if(text.charAt(mid) == '-') {

        return new String[]{text.substring(0, mid + 1), text.substring(mid + 1)};

      } else {

        return new String[]{text.substring(0, mid),text.substring(mid + 1)};

      }


    } else {

      HyphenGenerator hyphi = new HyphenGenerator("de");
      List<String> hyphenatedText = hyphi.hyphenate(text);

      if(hyphenatedText.size() > 1) {

        String subPoly1Text = "";
        String subPoly2Text = "";

        for(int j = 0; j < hyphenatedText.size(); j++) {

          if(j < hyphenatedText.size() / 2){
            subPoly1Text += hyphenatedText.get(j);
          } else {
            subPoly2Text += hyphenatedText.get(j);
          }

        }

        return new String[]{subPoly1Text, subPoly2Text};

      } else {

        int mid = text.length() / 2; //get the middle of the String
        return new String[]{text.substring(0, mid),text.substring(mid)};

      }
    }
  }


}
