import java.util.HashSet;

class VerticalTrapezoid {
  public LineSegment left;
  public LineSegment right;
  public LineSegment top;
  public LineSegment bot;

  public HashSet<VerticalTrapezoid> prev = new HashSet<VerticalTrapezoid>();
  public HashSet<VerticalTrapezoid> next = new HashSet<VerticalTrapezoid>();

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

  public VerticalTrapezoid(Vertex top, Vertex bot) {
    left = new LineSegment(top, bot);
  }

  public String toString() {

    String result = "";
    result += "[";
    result += ("(" + left + "),");
    result += ("(" + top + "),");
    result += ("(" + right + "),");
    result += ("(" + bot + "),");
    result += ("]");

    return result;
  }

  public void addPreviousTrapezoid(VerticalTrapezoid t) {
    if(t == null) System.out.println("Nullpointer");
    prev.add(t);
  }
  public void addNextTrapezoid(VerticalTrapezoid t) {
    next.add(t);
  }

  public HashSet<VerticalTrapezoid> getNext() {
    return next;
  }
  public HashSet<VerticalTrapezoid> getPrev() {
    return prev;
  }

  public boolean equals(Object o) {
    if(o instanceof VerticalTrapezoid) {
      VerticalTrapezoid t = (VerticalTrapezoid) o;
      if(t.left.start.equals(this.left.start)) return true;
    }
    return false;
  }
}
