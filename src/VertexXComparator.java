import java.util.Comparator;

class VertexXComparator implements Comparator<Vertex> {
    @Override
    public int compare(Vertex a, Vertex b) {
        return a.x < b.x ? -1 : a.x == b.x ? 0 : 1;
    }
}
 
