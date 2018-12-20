

class DefaultControllerState {
  private VertexPolygon selectedPolygon = null;
  private Boolean polygonOpacity = true;
  private Boolean _isCreatingNewPolygon = false;

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
}
