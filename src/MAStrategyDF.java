import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import javafx.scene.shape.Line;
import java.lang.Math;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Hashtable;

class MAStrategyDF implements TextStrategy{


  public MAStrategyDF() {}


  public void drawText(VertexPolygon originalPoly, Pane textLayer){
    double[] bb = originalPoly.getBoundingBox();
    int density = 1;
    //int k = Integer.parseInt(originalPoly.getText());
    int k = 4;
    Hashtable<String,Integer> edgeTable = new Hashtable<String, Integer>();
    ArrayList<Vertex> outlineList = buildOutlinePoints(originalPoly, edgeTable, density);
    int left = (int) bb[0];
    int right = (int) bb[1];
    int up = (int) bb[2];
    int down = (int) bb[3];

    for(int i = left; i <= right - density; i += density) {
      for(int j = up; j <= down - density; j += density) {
        double min1 = 10000;
        Integer min1edge = -1;
        Integer min2edge = -1;
        double min2 = 10000;
        for(Vertex v : outlineList) {
          double dist = distance(i,j,v,k);
          if(dist < min1){
            if(edgeTable.get(v.toString()).equals(min1edge)){
              min1 = dist;
            } else {
              min2 = min1;
              min2edge = min1edge;
              min1edge = edgeTable.get(v.toString());
            }
          } else if(dist < min2 && !edgeTable.get(v.toString()).equals(min1edge)) {
            min2 = dist;
          }
        }

        if(Math.abs(min2 - min1) < 0.07) {

          Circle point = new Circle();
          point.setCenterX(i);
          point.setCenterY(j);
          point.setRadius(1.0);
          point.setFill(Color.color(0,0,1,0.7));
          textLayer.getChildren().add(point);
        }
      }
    }

  }

  public double distance(int x, int y, Vertex u, int k) {
    return Math.sqrt(Math.pow(x - u.x, 2) + (Math.pow(y - u.y,2)/k));
  }

  public ArrayList<Vertex> buildOutlinePoints(VertexPolygon poly, Hashtable<String, Integer> table, int density) {
    ArrayList<Vertex> pointList = new ArrayList<Vertex>();
    for(int i = 0; i < poly.getOutline().size(); i++) {
      LineSegment edge = poly.getLineSegment(i);
      double length = edge.end.sub(edge.start).mag();
      Vertex directionV = edge.end.sub(edge.start).mult(1/length);
      for(int j = 1; j <= length - density; j += density) {
        Vertex newPoint = edge.start.add(directionV.mult(j));
        pointList.add(newPoint);
        table.put(newPoint.toString(), i);
      }
    }
    return pointList;
  }
}
