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

class PolygonReader {

  File file;

  public PolygonReader(File file) {
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

    JSONArray polygons = jsonObj.getJSONArray("polygons");



    ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();
    for(int i = 0; i < polygons.length(); i++) {
      JSONArray polygon = polygons.getJSONArray(i);
      VertexPolygon newPolygon = new VertexPolygon();

      for(int j = 0; j < polygon.length(); j++) {
        JSONObject _vertex = polygon.getJSONObject(j);
        Vertex v = new Vertex(_vertex.getDouble("x"), _vertex.getDouble("y"));
        newPolygon.addVertex(v);
      }

      polygonList.add(newPolygon);
    }

    return polygonList;
  }

  public String readFile(File file) throws Exception {
    String entireFileText = new Scanner(file)
        .useDelimiter("\\A").next();
    return (entireFileText);
  }

}
