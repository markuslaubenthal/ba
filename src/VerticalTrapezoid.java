

class VerticalTrapezoid {
  public LineSegment left;
  public LineSegment right;
  public LineSegment top;
  public LineSegment bot;

  public VerticalTrapezoid(LineSegment l, LineSegment t, LineSegment r, LineSegment b) {
    left = l;
    top = t;
    right = r;
    bot = b;
  }

  public VerticalTrapezoid(LineSegment l, LineSegment t, LineSegment b) {
    left = l;
    top = t;
    bot = b;
  }

  public VerticalTrapezoid(LineSegment l) {
    left = l;
  }

  public String toString() {
    return "Trapezoid";
  }
}
