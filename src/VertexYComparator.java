import java.util.Comparator;

class VertexYComparator implements Comparator<Vertex> {
    @Override
    public int compare(Vertex a, Vertex b) {
        return a.y < b.y ? -1 : a.y == b.y ? 0 : 1;
    }
}
