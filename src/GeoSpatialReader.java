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

    for(int i = 0; i < features.length(); i++) {

      JSONObject feature = features.getJSONObject(i);
      JSONObject geometry = feature.getJSONObject("geometry");
      JSONObject properties = feature.getJSONObject("properties");
      String text = properties.getString("NAME_ENGL");

      //String euFlag = properties.getString("OTHR_FLAG");

      //if(euFlag.equals("T")) continue;

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

    for(Vertex v : vertexList) {
      v.x = v.x * 400.0 / absoluteBiggestValue + 400.0;
      v.y = 800.0 - (v.y * 400.0 / absoluteBiggestValue + 400.0);
    }

    return polygonList;

  }

  public String readFile(File file) throws Exception {
    String entireFileText = new Scanner(file)
        .useDelimiter("\\A").next();
    return (entireFileText);
  }

}
