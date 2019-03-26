import java.util.Comparator;

class VerticalTrapezoidVertexComparator implements Comparator<ProxyVerticalTrapezoidVertex> {
    @Override
    public int compare(ProxyVerticalTrapezoidVertex a, ProxyVerticalTrapezoidVertex b) {
      if(a.t != null && b.v != null || a.v != null && b.t != null) {
        VerticalTrapezoid t;
        Vertex v;
        if(a.t != null && b.v != null) {
          t = a.t;
          v = b.v;
        } else {
          t = b.t;
          v = a.v;
        }

        LineSegment left = t.left;
        LineSegment top = t.top;
        LineSegment bot = t.bot;

        if(top.start.equals(v) || top.end.equals(v) ||
          bot.start.equals(v) || bot.end.equals(v) ) return 0;

        LineSegment vertical = new LineSegment(new Vertex(v.x,0), new Vertex(v.x,1000));
        Vertex intersectionTop = new Vertex(0,0);
        Vertex intersectionBot = new Vertex(0,0);
        vertical.getLineIntersection(top, intersectionTop);
        vertical.getLineIntersection(bot, intersectionBot);

        if(v.y < intersectionTop.y) return -1;
        return 1;
      } else {
        return a.t.left.start.y < b.t.left.start.y ? -1 : 1;
      }
    }
}
