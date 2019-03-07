class MAVertex extends Vertex {

  MAVertex pred = null;
  MAVertex succ = null;
  MAVertex parent = null;


  public MAVertex(double x, double y) {
    super(x,y);
  }

  public void setPred(MAVertex pred) {
    this.pred = pred;
  }

  public int getPred() {
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

  public String toString() {
    return "x:" + x + "y:" + y ;
  }
}
