import java.util.ArrayList;

class DefaultControllerState {
  private VertexPolygon selectedPolygon = null;
  private Boolean polygonOpacity = true;
  private Boolean _isCreatingNewPolygon = false;
  private ArrayList<Vertex> vertices = new ArrayList<Vertex>();

  public VertexPolygon getSelectedPolygon() {
    return selectedPolygon;
  }
  public void setSelectedPolygon(VertexPolygon polygon) {
    selectedPolygon = polygon;
  }
  public Boolean togglePolygonOpacity() {
    polygonOpacity = !polygonOpacity;
    return polygonOpacity;
  }
  public Boolean getPolygonOpacity() {
    return polygonOpacity;
  }
  public Boolean isBuildingNewPolygon() {
    return _isCreatingNewPolygon;
  }
  public Boolean startBuildingNewPolygon() {
    _isCreatingNewPolygon = true;
    return _isCreatingNewPolygon;
  }
  public Boolean stopBuildingNewPolygon() {
    _isCreatingNewPolygon = false;
    return _isCreatingNewPolygon;
  }
  public void addVertex(Vertex v) {
    vertices.add(v);
  }
  public void resetVertices() {
    vertices = new ArrayList<Vertex>();
  }
  public Boolean containsVertex(Vertex v) {
    return vertices.contains(v);
  }
  public ArrayList<Vertex> getVertices() {
    return vertices;
  }
}
