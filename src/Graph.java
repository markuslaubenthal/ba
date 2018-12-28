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

    for(GraphVertex v : vertecies) {

      int upperCount = 0;
      int lowerCount = 0;

      GraphVertex pointer = v;

      while(pointer.getUpper() != null) {
        upperCount++;
        pointer = pointer.getUpper();
      }

      pointer = v;

      while(pointer.getLower() != null) {
        lowerCount++;
        pointer = pointer.getLower();
      }

      int score = -(density - 1) - Math.abs(upperCount - lowerCount) + upperCount + lowerCount;

      v.setScore(score);

    }

  }

  public void addEdge(GraphVertex v, GraphVertex u) {
    v.addNeighbour(u);
  }

  public double distance(GraphVertex v, GraphVertex u) {
    return u.getScore() - Math.abs(v.y - u.y) * density / minSize;
  }

  public ArrayList<GraphVertex> findLongestPathGreedy() {

    ArrayList<GraphVertex> longestPath = new ArrayList<GraphVertex>();
    double longestPathLength = -9999.0;

    for(GraphVertex v : vertecies) {

      ArrayList<GraphVertex> path = new ArrayList<GraphVertex>();
      path.add(v);
      double pathLength = v.score;

      GraphVertex pointer = v;

      while(pointer.neighbours.size() > 0) { // laufe durch den graphen von v aus

        GraphVertex next = null;
        double biggestDistance = -9999.9;

        for(GraphVertex u : pointer.neighbours) { //finde groessten nachbar

          if(biggestDistance < distance(pointer,u)) {
            biggestDistance = distance(pointer,u);
            next = u;
          }

        }

        pathLength += biggestDistance;
        path.add(next);
        pointer = next;

      }

      if(longestPathLength < pathLength){
        longestPathLength = pathLength;
        longestPath = path;
      }

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
