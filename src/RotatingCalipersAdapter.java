import java.awt.geom.Point2D;
import java.util.ArrayList;

class RotatingCalipersAdapter {
  public static ArrayList<Vertex> getMinimumBoundingRectangle(ArrayList<Vertex> vertices) {

    double[] xs, ys;
    xs = new double[vertices.size()];
    ys = new double[vertices.size()];
    for(int i = 0; i < vertices.size(); i++) {
      xs[i] = vertices.get(i).x;
      ys[i] = vertices.get(i).y;
    }

    ArrayList<Vertex> corners = new ArrayList<Vertex>();
    try {
      Point2D.Double[] points = RotatingCalipers.getMinimumBoundingRectangle(xs, ys);
      for(int i = 0; i < points.length; i++) {
        corners.add(new Vertex(points[i].x, points[i].y));
      }
    } catch (Exception e) {

    }
    return corners;
  }

  public static Vertex getMinimumBoundingOrientation(ArrayList<Vertex> vertices) {
    ArrayList<Vertex> res = getMinimumBoundingRectangle(vertices);
    Vertex orientation = new Vertex(0,0);
    for(int i = 0; i < 2; i++) {
      if(res.get(i).sub(res.get(i + 1)).mag() > orientation.mag()) orientation = res.get(i).sub(res.get(i + 1));
    }
    return orientation;
  }
}
