

class ProxyVerticalTrapezoidVertex {
  public VerticalTrapezoid t = null;
  public Vertex v = null;

  public ProxyVerticalTrapezoidVertex(VerticalTrapezoid t) {
    this.t = t;
  }
  public ProxyVerticalTrapezoidVertex(Vertex v) {
    this.v = v;
  }

  public String toString() {
    if(t != null) return t.toString();
    if(v != null) return v.toString();
    return "Keine ahnung";
  }
}
