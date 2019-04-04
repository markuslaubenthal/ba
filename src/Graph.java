import java.util.ArrayList;
import java.util.HashSet;

class Graph {

  ArrayList<GraphVertex> vertecies = new ArrayList<GraphVertex>();

  VertexPolygon poly;
  double minSize;
  int density;

  public Graph(VertexPolygon poly, double minSize, int density) {
    this.poly = poly;
    this.minSize = minSize;
    this.density = density;
  }

  public void generateNetwork(){
    double stepsize = minSize / density;
    double[] bb = poly.getBoundingBox();
    double left = bb[0];
    double right = bb[1];
    double up = bb[2];
    double down = bb[3];

    ArrayList<GraphVertex> lastColumn = new ArrayList<GraphVertex>();

    for(int i = 0; left + i * stepsize <= right; i++) {

      ArrayList<GraphVertex> thisColumn = new ArrayList<GraphVertex>();

      for(int j = 0; up + j * stepsize <= down; j++) {

        GraphVertex v = new GraphVertex(left + i * stepsize, up + j * stepsize);

        if(poly.vertexInPolygon(v)) {

          vertecies.add(v);
          thisColumn.add(v);

          if(lastColumn.size() > 0) {
            for(GraphVertex u : lastColumn) {
              u.addNeighbour(v);
            }
          }

          if(thisColumn.size() > 1){

            GraphVertex upper = thisColumn.get(thisColumn.size()-2);
            if(Geometry.canSee(poly, v, upper)) {
              upper.setLower(v);
              v.setUpper(upper);
            }
          }

        }

      }

      lastColumn = thisColumn;

    }

    calculateScores();

  }

  private void calculateScores() {

    int score = 0;

    for(GraphVertex v : vertecies) {

      score += 2;
      if(v.getUpper() == null) score = -1 * density + 1;

      v.setScore(score);

    }

    for(int i = vertecies.size() - 1; i > 0; i -= 1) {

      score += 2;
      if(vertecies.get(i).getLower() == null) score = -1 * density + 1; // - 2 * density for padding - not realy

      vertecies.get(i).setScore(Math.min(score, vertecies.get(i).getScore()));

    }

  }

  public void addEdge(GraphVertex v, GraphVertex u) {
    v.addNeighbour(u);
  }

  public int distance(GraphVertex v, GraphVertex u) {
    return (int)(u.getScore() - (Math.abs(v.y - u.y) * density) / minSize); // irgend was ist faul hier
  }

  public ArrayList<GraphVertex> findLongestPath() {

    if(vertecies.size() == 0) return null;

    /*
    Initialize the pathLength with the score so that we take the own value into account.
     */

    for(GraphVertex v : vertecies) { v.pathLengthEndingHere = v.score; }

    for(GraphVertex v : vertecies) {

      for(GraphVertex w : v.neighbours) {

        if(v.pathLengthEndingHere + distance(v,w) > w.pathLengthEndingHere) {

          w.parent = v;
          w.pathLengthEndingHere = v.pathLengthEndingHere + distance(v,w);

        }

      }

    }

    GraphVertex winner = vertecies.get(0);

    for(GraphVertex v : vertecies) {

      if(v.pathLengthEndingHere > winner.pathLengthEndingHere) winner = v;

    }

    ArrayList<GraphVertex> longestPath = new ArrayList<GraphVertex>();

    GraphVertex pointer = winner;

    while(pointer != null) {

      longestPath.add(0, pointer);
      pointer = pointer.parent;

    }

    return longestPath;

  }

  public String toString() {
    String s = "";
    for(GraphVertex v : vertecies) {
      s += v.toString();
    }
    return s;
  }
}
























//
