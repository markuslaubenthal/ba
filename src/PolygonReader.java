import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;







class PolygonReader {

  String filename;

  public PolygonReader(String filename) {
    this.filename = filename;
  }

  public ArrayList<VertexPolygon> get() {
    try {
      return getForReal();
    } catch(Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public ArrayList<VertexPolygon> getForReal() throws Exception {
    InputStream is = new FileInputStream(filename);
    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
    String line = buf.readLine();
    StringBuilder sb = new StringBuilder();

    while(line != null){
       sb.append(line).append("\n");
       line = buf.readLine();
    }

    String fileAsString = sb.toString();


    JSONObject jsonObj = new JSONObject(fileAsString);
    System.out.println(jsonObj.toString());

    JSONArray polygons = jsonObj.getJSONArray("polygons");


    ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();
    for(int i = 0; i < polygons.length(); i++) {
      JSONArray polygon = polygons.getJSONArray(i);
      VertexPolygon newPolygon = new VertexPolygon();

      for(int j = 0; j < polygon.length(); j++) {
        JSONObject _vertex = polygon.getJSONObject(i);
        Vertex v = new Vertex(_vertex.getDouble("x"), _vertex.getDouble("y"));
        newPolygon.addVertex(v);
      }

      polygonList.add(newPolygon);
    }

    return polygonList;
  }

}
