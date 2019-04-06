import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;


class VertexYComparator implements Comparator<Vertex> {
    @Override
    public int compare(Vertex a, Vertex b) {
        return a.y < b.y ? -1 : a.y == b.y ? 0 : 1;
    }

    public static void sort(ArrayList<Vertex> vertices) {
      Collections.sort(vertices, new VertexYComparator());
    }
}
