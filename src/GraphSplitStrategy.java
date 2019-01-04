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
  // try {

    VertexPolygon poly = Geometry.scalePolygon(originalPoly, 0.8);

    int density = 4;

    ArrayList<VertexPolygon> subPolygonList = new ArrayList<VertexPolygon>();
    subPolygonList.add(poly);

    for(int bbb = 0; bbb < poly.text.length(); bbb++) {

      ArrayList<VertexPolygon> newSubPolygonList = new ArrayList<VertexPolygon>();

      for(VertexPolygon p : subPolygonList) {
        if(p.text.length() < 3) continue;

        double[] bb = p.getBoundingBox();
        double minSize = (bb[1]-bb[0]) / (2 * p.getText().length());
        Graph g = new Graph(p, minSize, density);
        g.generateNetwork();
        ArrayList<GraphVertex> path = g.findLongestPath();
        int verteciesPerLetter = path.size() / p.getText().length();
        int avgScore = 0;

        for(GraphVertex v : path) { avgScore += v.getScore(); }

        avgScore = avgScore / path.size();


        if(avgScore + density - 1 > 3 * verteciesPerLetter) {

          /*
          Split the text into two parts based on word seperations like space or dash,
          hyphenation or by simply splitting the text in the center.
           */

          String[] textParts = splitText(p.text);

          /*
          Create subpolygons with the area ratio matching the ratio of the two text parts.
           */



          double upperRatio = (double)textParts[0].length() / p.text.length();
          double lowerRatio = (double)textParts[1].length() / p.text.length();

          VertexPolygon[] polygonParts = Geometry.findSplitLineApprox(p, upperRatio, lowerRatio);

          //>>> DEBUG
          /*
          Polygon polyy = new Polygon();

          for(Vertex v : polygonParts[0].getOutline()) {
            polyy.getPoints().addAll(new Double[] {v.x, v.y} );
          }
          polyy.setFill(Color.color(0,0,0,0));
          polyy.setStrokeWidth(1);
          polyy.setStroke(Color.RED);
          textLayer.getChildren().add(polyy);
          */
          //>>> DEBUG

          polygonParts[0].text = textParts[0];
          polygonParts[1].text = textParts[1];
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);

        } else {

          newSubPolygonList.add(p);

        }

      }

      subPolygonList = newSubPolygonList;

    }


    for(VertexPolygon p : subPolygonList) {
      p.setTextStrategy(new GraphStrategy(1));
      p.drawText(textLayer);
      p.setTextStrategy(this);
    }
  // } catch (Exception e){
  //   System.out.println(e);
  // }

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

        return new String[]{subPoly1Text + "-", subPoly2Text};

      } else {

        int mid = text.length() / 2; //get the middle of the String
        return new String[]{text.substring(0, mid) + "-",text.substring(mid)};

      }
    }
  }


}
