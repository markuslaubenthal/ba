class MAVertex{

  MAVertex pred = null;
  MAVertex succ = null;
  MAVertex parent = null;
  MALineSegment nextEdge = null;
  double x;
  double y;


  public MAVertex(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void setPred(MAVertex pred) {
    this.pred = pred;
  }

  public MAVertex getPred() {
    return pred;
  }

  public void setSucc(MAVertex succ) {
    this.succ = succ;
  }

  public MAVertex getSucc() {
    return succ;
  }

  public void setParent(MAVertex parent) {
    this.parent = parent;
  }

  public MAVertex getParent() {
    return parent;
  }

  public void setNextEdge(MALineSegment l) {
    this.nextEdge = l;
  }

  public MALineSegment getNextEdge() {
    return nextEdge;
  }

  /**
   * Berechnet die Distanz zwischen 2 Vertices.
   * @method distance
   */
  public double distance(MAVertex v) {
      return sub(v).mag();
  }

  /**
   * Subtrahiert den übergebenen MAVertex von sich selbst.
   * @method sub
   */
  public MAVertex sub(MAVertex vertex) {
      return new MAVertex(this.x - vertex.x, this.y - vertex.y);
  }

  /**
   * Addiert den übergebenen MAVertex auf sich selbst.
   * @method add
   */
  public MAVertex add(MAVertex vertex) {
      return new MAVertex(this.x + vertex.x, this.y + vertex.y);
  }

  /**
   * Multipliziert den Vertex mit einem Skalar
   * @method mult
   */
  public MAVertex mult(double scalar) {
      return new MAVertex(this.x * scalar, this.y * scalar);
  }

  /**
   * Berechnet die Länge des MAVertex
   * @method mag
   */
  public double mag() {
      return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  /**
   * Berechnet das Skalarprodukt von 2 Vektoren
   * @method dot
   */
  public double dot(MAVertex vertex) {
      return this.x * vertex.x + this.y * vertex.y;
  }

  /**
   * Berechnet das Kreuzprodukt von 2 Vektoren
   * @method cross
   */
  public double cross(MAVertex vertex) {
      return this.y * vertex.x - this.x * vertex.y;
  }

  public String toString() {
    return "x:" + x + "y:" + y ;
  }
}
