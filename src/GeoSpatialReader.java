import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.util.Scanner;

class GeoSpatialReader {

  File file;

  public GeoSpatialReader(File file) {
    this.file = file;
  }

  public ArrayList<VertexPolygon> get() {
    try {
      String content = readFile(file);
      return stringToList(content);
    } catch(Exception e) {
      return null;
    }
  }


  private ArrayList<VertexPolygon> stringToList(String content) throws Exception {

    JSONObject jsonObj = new JSONObject(content);

    JSONArray features = jsonObj.getJSONArray("features");

    ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();
    ArrayList<Vertex> vertexList = new ArrayList<Vertex>();

    double absoluteBiggestValue = -1.0;
    double smallestValueX = Double.POSITIVE_INFINITY;
    double smallestValueY = Double.POSITIVE_INFINITY;

    for(int i = 0; i < features.length(); i++) {

      JSONObject feature = features.getJSONObject(i);
      JSONObject geometry = feature.getJSONObject("geometry");
      JSONObject properties = feature.getJSONObject("properties");
      //if(properties.has("CNTR_CODE") && !properties.getString("CNTR_CODE").equals("DE")) continue;

      String text = "null";
      if(properties.has("NAME_ENGL")) text = properties.getString("NAME_ENGL");
      if(properties.has("NUTS_NAME")) text = properties.getString("NUTS_NAME");

      JSONArray coordinateList = geometry.getJSONArray("coordinates");

      if(geometry.getString("type").equals("Polygon")) {
        coordinateList = new JSONArray().put(coordinateList);
      }

      for(int z = 0; z < coordinateList.length(); z++){

        JSONArray coordinates = coordinateList.getJSONArray(z).getJSONArray(0);

        VertexPolygon newPolygon = new VertexPolygon();

        if(coordinates.length() < 3) continue;

        for(int j = 0; j < coordinates.length(); j++) {
          JSONArray vector = coordinates.getJSONArray(j);
          Vertex v = new Vertex(vector.getDouble(0), vector.getDouble(1));

          if(Math.abs(v.x) > absoluteBiggestValue) absoluteBiggestValue = Math.abs(v.x);
          if(Math.abs(v.y) > absoluteBiggestValue) absoluteBiggestValue = Math.abs(v.y);
          if(v.x < smallestValueX) smallestValueX = v.x;
          if(v.y < smallestValueY) smallestValueY = v.y;

          if(!vertexList.contains(v)) {
            vertexList.add(v);
            newPolygon.addVertex(v);
          } else {
            v = vertexList.get(vertexList.indexOf(v));
            newPolygon.addVertex(v);
          }
        }

        newPolygon.setText(text);
        polygonList.add(newPolygon);
      }

    }

    double scaleFactor = absoluteBiggestValue - Math.max(smallestValueX, smallestValueY);
    for(Vertex v : vertexList) {
      v.x = (v.x - smallestValueX) * 780.0 / scaleFactor + 20;
      v.y = 760.0 - ((v.y - smallestValueY) * 760.0 / scaleFactor) + 20;
    }

    return polygonList;

  }

  public String readFile(File file) throws Exception {
    String entireFileText = new Scanner(file)
        .useDelimiter("\\A").next();
    return (entireFileText);
  }

}
