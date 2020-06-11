import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ConvexHull {
    // private static class Point implements Comparable<Point> {
    //     public int x, y;
    //
    //     public Point(int x, int y) {
    //         this.x = x;
    //         this.y = y;
    //     }
    //
    //     @Override
    //     public int compareTo(Point o) {
    //         return Integer.compare(x, o.x);
    //     }
    //
    //     @Override
    //     public String toString() {
    //         return String.format("(%d, %d)", x, y);
    //     }
    // }

    public static ArrayList<Vertex> convexHull(ArrayList<Vertex> p) {

      // List<Point> p = new ArrayList<Point>();
      // for(int i = 0; i < p.size(); i++) {
      //   p.add(new Point(v.get(i).x, v.get(i).y));
      // }

        // if (p.isEmpty()) return emptyList();
        p.sort(Vertex::compareTo);
        ArrayList<Vertex> h = new ArrayList<>();

        // lower hull
        for (Vertex pt : p) {
            while (h.size() >= 2 && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }

        // upper hull
        int t = h.size() + 1;
        for (int i = p.size() - 1; i >= 0; i--) {
            Vertex pt = p.get(i);
            while (h.size() >= t && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }

        h.remove(h.size() - 1);

        // List<Vertex> vertices = new ArrayList();
        // for(int i = 0; i < h.size(); i++) {
        //   vertices.add(new Vertex(h.get(i).x, h.get(i).y));
        // }

        return h;
    }

    // ccw returns true if the three points make a counter-clockwise turn
    private static boolean ccw(Vertex a, Vertex b, Vertex c) {
        return ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x));
    }
}
