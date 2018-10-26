import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

class PolygonWriter {

  ArrayList<VertexPolygon> polygonList;

  public PolygonWriter(ArrayList<VertexPolygon> polygonList) {
    this.polygonList = polygonList;
  }

  public void save() {
    try {
      saveForReal();
    }
    catch(Exception e) {

    }
  }

  private void saveForReal() throws Exception {
    JSONObject root = new JSONObject();
    JSONArray polygonObjects = new JSONArray();

    for(VertexPolygon p : polygonList) {
      JSONArray polygon = new JSONArray();
      JSONObject vertex = new JSONObject();
      ArrayList<Vertex> outline = p.getOutline();

      for(int i = 0; i < outline.size(); i++) {
        vertex.put("x", outline.get(i).x);
        vertex.put("y", outline.get(i).y);
        polygon.put(vertex);
      }

      polygonObjects.put(polygon);
    }

    root.put("polgyons", polygonObjects);
    System.out.println(root.toString());
  }

}
