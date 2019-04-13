import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

class GraphCenterX {

  ArrayList<Vertex> vertecies = new ArrayList<Vertex>();

  public GraphCenterX() {
  }

  public void addEdge(Vertex v, Vertex u) {
    v.addNeighbour(u);
  }

  public void addVertex(Vertex v) {
    vertecies.add(v);
  }


  public double distance(Vertex v, Vertex u) {
    return Math.abs(u.x - v.x);
  }

  public ArrayList<Vertex> findLongestPath() {

    Collections.sort(vertecies, new VertexXComparator());

    for(Vertex v : vertecies) { v.pathLengthEndingHere = 1; }

    if(vertecies.size() == 0) return null;

    for(Vertex v : vertecies) {


      for(Vertex w : v.neighbours) {

        if(v.pathLengthEndingHere + distance(v,w) > w.pathLengthEndingHere) {
          w.parent = v;
          w.pathLengthEndingHere = v.pathLengthEndingHere + distance(v,w);

        }

      }

    }

    Vertex winner = vertecies.get(0);

    for(Vertex v : vertecies) {

      if(v.pathLengthEndingHere > winner.pathLengthEndingHere) winner = v;

    }

    ArrayList<Vertex> longestPath = new ArrayList<Vertex>();

    Vertex pointer = winner;

    while(pointer != null) {

      longestPath.add(0, pointer);
      pointer = pointer.parent;

    }

    return longestPath;

  }

  public String toString() {
    String s = "";
    for(Vertex v : vertecies) {
      s += v.toString();
    }
    return s;
  }
}
























//
