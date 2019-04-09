import java.util.HashSet;

class VerticalTrapezoid {
  public LineSegment left;
  public LineSegment right;
  public LineSegment top;
  public LineSegment bot;

  public double informationLeft;
  public double informationRight;

  public HashSet<VerticalTrapezoid> prev = new HashSet<VerticalTrapezoid>();
  public HashSet<VerticalTrapezoid> next = new HashSet<VerticalTrapezoid>();

  public boolean active = true;

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
    prev.add(t);
  }
  public void addNextTrapezoid(VerticalTrapezoid t) {
    next.add(t);
  }

  public VerticalTrapezoid getNextExplicit() {
    for(VerticalTrapezoid t : next) {
      if(t.isActive()) {
        return t;
      }
    }
    return null;
  }

  public VerticalTrapezoid getPrevExplicit() {
    for(VerticalTrapezoid t : prev) {
      if(t.isActive()) {
        return t;
      }
    }
    return null;
  }

  public HashSet<VerticalTrapezoid> getNext() {
    return next;
  }
  public HashSet<VerticalTrapezoid> getPrev() {
    return prev;
  }

  public void removeNext(VerticalTrapezoid t) {
    if(next.contains(t)) next.remove(t);
  }

  public void removePrev(VerticalTrapezoid t) {
    if(prev.contains(t)) prev.remove(t);
  }

  public boolean equals(Object o) {
    if(o instanceof VerticalTrapezoid) {
      VerticalTrapezoid t = (VerticalTrapezoid) o;
      if(t.left.start.equals(this.left.start)) return true;
    }
    return false;
  }

  public double area() {
    //1/2(a+c) * h;
    return 1.0/2.0 * (Math.abs(left.getHeight() + right.getHeight())) * Math.abs(left.start.x - right.start.x);
  }

  public boolean contains(Vertex v) {
    if(top.contains(v) || bot.contains(v)) return true;
    return false;
  }

  public boolean hasNext() {
    return next.size() == 0 ? false : true;
  }

  public boolean hasNextExplicit() {
    for(VerticalTrapezoid t : next) {
      if(t.isActive()) return true;
    }
    return false;
  }
  public boolean hasPrev() {
    return prev.size() == 0 ? false : true;
  }

  public boolean isActive() {
    return active;
  }

  public void activate() {
    active = true;
  }

  public void deactivate(int direction) {
    active = false;
    if(direction == 1) {
      for(VerticalTrapezoid t : next) {
        t.deactivate(direction);
      }
    }
    if(direction == -1) {
      for(VerticalTrapezoid t : prev) {
        t.deactivate(direction);
      }
    }
  }
}
