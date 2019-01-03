import java.util.ArrayList;

class GraphVertex extends Vertex {

  ArrayList<GraphVertex> neighbours = new ArrayList<GraphVertex>();

  GraphVertex upper = null;
  GraphVertex lower = null;

  GraphVertex parent = null;
  int pathLengthEndingHere = 0;

  int score = -1;

  public GraphVertex(double x, double y) {
    super(x,y);
  }

  public void addNeighbour(GraphVertex v){
    neighbours.add(v);
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public void setUpper(GraphVertex upper) {
    this.upper = upper;
  }

  public GraphVertex getUpper() {
    return upper;
  }

  public void setLower(GraphVertex lower) {
    this.lower = lower;
  }

  public GraphVertex getLower() {
    return lower;
  }

  public String toString() {
    return "score:" + score + "x:" + x + "y:" + y ;
  }
}
