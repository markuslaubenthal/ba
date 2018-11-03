import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.PrintWriter;

class PolygonWriter {

  ArrayList<VertexPolygon> polygonList;

  public PolygonWriter(ArrayList<VertexPolygon> polygonList) {
    this.polygonList = polygonList;
  }

  public void save(String filename) {
    try {
      saveForReal(filename);
    }
    catch(Exception e) {

    }
  }

  private void saveForReal(String filename) throws Exception {
    JSONObject root = new JSONObject();
    JSONArray polygonObjects = new JSONArray();

    for(VertexPolygon p : polygonList) {
      JSONArray polygon = new JSONArray();
      ArrayList<Vertex> outline = p.getOutline();

      for(int i = 0; i < outline.size(); i++) {
        JSONObject vertex = new JSONObject();
        vertex.put("x", outline.get(i).x);
        vertex.put("y", outline.get(i).y);
        polygon.put(vertex);
      }

      polygonObjects.put(polygon);
    }

    root.put("polygons", polygonObjects);
    PrintWriter out = new PrintWriter(filename);
    out.print(root.toString());
    out.close();
    // System.out.println(root.toString());
  }

}
