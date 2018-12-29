import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;

class GraphSplitStrategy implements TextStrategy {

  VertexPolygon poly;
  Pane textLayer;

  public GraphSplitStrategy() {}

  public void drawText(VertexPolygon poly, Pane textLayer) {

    ArrayList<VertexPolygon> subPolygonList = new ArrayList<VertexPolygon>();
    String[] textParts = splitText(poly.text);
    VertexPolygon[] polygonParts = Geometry.findSplitLineApprox(poly);
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
      return new String[] {subPoly1Text, subPoly2Text};
    } else {
      int mid = text.length() / 2; //get the middle of the String
      return new String[] {text.substring(0, mid),text.substring(mid)};
    }

  }


}
