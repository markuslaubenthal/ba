

class VertexWrapper {
  private Vertex v;

  private VertexWrapper next;
  private VertexWrapper prev;

  public VertexWrapper(Vertex v) {
    this.v = v;
  }

  public void setNext(Vertex n) {
    next = new VertexWrapper(n);
  }

  public void setPrev(Vertex n) {
    prev = new VertexWrapper(n);
  }

  public void setNext(VertexWrapper n) {
    next = n;
  }

  public void setPrev(VertexWrapper n) {
    prev = n;
  }

  public Vertex value() {
    return v;
  }

  public VertexWrapper getNext() {
    return next;
  }

  public VertexWrapper getPrev() {
    return prev;
  }
}
