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
    try {

    VertexPolygon poly = Geometry.scalePolygon(originalPoly, 1);


    ArrayList<VertexPolygon> subPolygonList = new ArrayList<VertexPolygon>();
    subPolygonList.add(poly);

    for(int bbb = 0; bbb < poly.text.length(); bbb++) {

      ArrayList<VertexPolygon> newSubPolygonList = new ArrayList<VertexPolygon>();

      for(VertexPolygon p : subPolygonList) {

        if(p.text.length() < 3) {
          // System.out.println("verteciesPerLetter == 0");
          newSubPolygonList.add(p);
          continue;
        }

        // int density = Math.max(10, 50 / p.getText().length());
        int density = Math.max(8, 8 * 4 / poly.getText().length());
        //double[] bb = p.getBoundingBox();
        //double minSize = (bb[1]-bb[0]) / (2 * p.getText().length());
        double minSize = Math.sqrt(p.getAreaSize()/ poly.getText().length() / 3);

        Graph g = new Graph(p, minSize, density);
        g.generateNetwork();
        ArrayList<GraphVertex> path = g.findLongestPath();

        if(path == null) {
          // System.out.println("path == null");
          //System.out.println(p.text);
          VertexPolygon[] polygonParts = slicePoly(p, density);
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);
          continue;
        }

        int verteciesPerLetter = path.size() / p.getText().length();

        if(verteciesPerLetter == 0) {
          // System.out.println("verteciesPerLetter == 0");
          // System.out.println(p.text);
          VertexPolygon[] polygonParts = slicePoly(p, density);
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);
          continue;
        }

        int avgScore = 0;
        int score = 0;

        for(GraphVertex v : path) { score += v.getScore(); }

        avgScore = score / path.size();

        if(avgScore + density - 1 < 1.5 * density / verteciesPerLetter) { // area covered is not 50% of the polygon
          System.out.println("too small");
          
          VertexPolygon[] polygonParts = slicePoly(p, density);
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);
          continue;
        }

        if(avgScore + density - 1 > 4 * verteciesPerLetter) { // the avg height can be 2.66 times higher than the width
          VertexPolygon[] polygonParts = slicePoly(p, density);
          newSubPolygonList.add(polygonParts[0]);
          newSubPolygonList.add(polygonParts[1]);
          continue;

        } else {
          // System.out.println("else");
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
  } catch (Exception e) {
      System.out.println(e);
  }
  }

  private VertexPolygon[] slicePoly(VertexPolygon p, int density) {
    try {

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

    VertexPolygon[] polygonParts = Geometry.findSplitLineApprox(p, upperRatio, lowerRatio, density);

    if(polygonParts == null) {
      polygonParts = Geometry.findSplitLineApprox(p, upperRatio, lowerRatio, density * density);
    }

    if(polygonParts == null) {
      polygonParts = Geometry.findSplitLineApprox(p, 0.5, 0.5, density * density);
    }

    polygonParts[0].text = textParts[0];
    polygonParts[1].text = textParts[1];

    return polygonParts;

    } catch (Exception e) {
        System.out.println(e);
    }

    return null;

  }


  private String[] splitText(String text) {

    if(text.substring(0,text.length() - 1).contains(" ") || text.substring(0,text.length() - 1).contains("-")){

      /*
      loop through text to find positions of spaces and dashes
      wich naturally seperate the text.
      Retruns the two substrings with the most center seperator.
       */

      ArrayList<Integer> indexList = new ArrayList<Integer>();

      for (int i = 0; i < text.length() - 1; i++){

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

        // return new String[]{subPoly1Text + "-", subPoly2Text};
        return new String[]{subPoly1Text, subPoly2Text};

      } else {

        int mid = text.length() / 2; //get the middle of the String
        // return new String[]{text.substring(0, mid) + "-",text.substring(mid)};
        return new String[]{text.substring(0, mid),text.substring(mid)};

      }
    }
  }


}
