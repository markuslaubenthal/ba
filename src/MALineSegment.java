import java.lang.Math;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

class MALineSegment{

  double r;
  MAVertex center = null;
  MAVertex start = null;
  MAVertex end = null;

  public MALineSegment(MAVertex v, MAVertex w) {
    this.start = v;
    this.end = w;
  }


  public void calcCandR() {

    MAVertex a = start.getPred();
    MAVertex b = start;
    MAVertex c = end;
    MAVertex d = end.getSucc();
    // calculate bisection lines
    double angleABC = angle(a,b,c) / 2.0;
    double angleBCD = angle(b,c,d) / 2.0;
    MAVertex bisecOrientationVectABC = rotate(c.sub(b), angleABC);
    MAVertex bisecOrientationVectBCD = rotate(d.sub(c), angleBCD);
    MALineSegment bisecABC = new MALineSegment(b, b.add(bisecOrientationVectABC));
    MALineSegment bisecBCD = new MALineSegment(c, c.add(bisecOrientationVectBCD));
    // find intersection and calculate the distance from the linesegement
    MAVertex intersectionPoint = new MAVertex(0,0);
    bisecABC.getLineIntersection(bisecBCD, intersectionPoint);
    this.center = intersectionPoint;
    this.r = distanceFromPoint(center);

  }


  


  public double angle(MAVertex x, MAVertex y, MAVertex z) {

    MAVertex v1 = x.sub(y);
    MAVertex v2 = z.sub(y);

    double angle = Math.atan2(v1.y, v1.x) - Math.atan2(v2.y, v2.x);
    if (angle < 0) { angle += 2 * Math.PI; }


    // angle between - pi and pi

    return angle;
  }

  public double distanceFromPoint(MAVertex p) {

    double l2 = end.sub(start).dot(end.sub(start)); // |w-v|^2
    if (l2 == 0.0) return p.distance(start); // v == w case
    // Consider the line extending the segment, parameterized as v + t (w - v).
    // We find projection of point p onto the line.
    // It falls where t = [(p-v) . (w-v)] / |w-v|^2
    // We clamp t from [0,1] to handle points outside the segment vw.
    double t = Math.max(0, Math.min(1, p.sub(start).dot(end.sub(start)) / l2));
    MAVertex projection = start.add(end.sub(start).mult(t));  // Projection falls on the segment
    return p.distance(projection);
    //https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment

  }

  public MAVertex rotate(MAVertex v, double d) {

    double x = Math.cos(d) * v.x - Math.sin(d) * v.y;
    double y = Math.sin(d) * v.x + Math.cos(d) * v.y;
    v.x = x;
    v.y = y;

    return v;

  }

  // STOLEN FROM LINESEGMENT weil ich zu dumm bin um das zu inheriten

  public boolean intersects(MALineSegment l) {
    return getLineIntersection(l, new MAVertex(0, 0));
  }

  public boolean getLineIntersection(MALineSegment l, MAVertex i) {
    return getLineIntersection(this.start.x, this.start.y, this.end.x, this.end.y,
      l.start.x, l.start.y, l.end.x, l.end.y, i);
  }

  public boolean getLineIntersection(double p0_x, double p0_y, double p1_x, double p1_y,
    double p2_x, double p2_y, double p3_x, double p3_y, MAVertex i)
  {
      double s1_x, s1_y, s2_x, s2_y;
      s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
      s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

      double s, t;
      s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
      t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);


      i.x = p0_x + (t * s1_x);
      i.y = p0_y + (t * s1_y);
      return true;

  }

  public String toString(){
    return "start:x:" + this.start.x + ",y:" + this.start.y + "end:x:" + this.end.x + ",y:" + this.end.y;
  }

  // STEAL END

}
