import java.lang.Math;

class MALineSegment extends LineSegment {

  double r;
  MAVertex center;

  public MALineSegment(MAVertex v, MAVertex w) {
    super(v,w);
    calcCandR();
  }

  public MAVertex calcCandR() {

    MAVertex a = start.getPred();
    MAVertex b = start;
    MAVertex c = end;
    MAVertex d = end.getSucc();
    // calculate bisection lines
    angleABC = angle(a,b,c);
    angleBCD = angle(b,c,d);
    MAVertex bisecOrientationVectABC = rotate(c.sub(b), angleABC);
    MAVertex bisecOrientationVectBCD = rotate(d.sub(c), angleBCD);
    MALineSegment bisecABC = new MALineSegment(b, b.add(bisecOrientationVectABC.mult(1000/bisecOrientationVectABC.mag())));
    MALineSegment bisecBCD = new MALineSegment(c, c.add(bisecOrientationVectBCD.mult(1000/bisecOrientationVectBCD.mag())));
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


}
