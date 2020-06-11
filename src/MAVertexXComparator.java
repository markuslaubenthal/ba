import java.util.Comparator;

class MAVertexXComparator implements Comparator<MAVertex> {
    @Override
    public int compare(MAVertex a, MAVertex b) {
        return a.x < b.x ? -1 : a.x == b.x ? 0 : 1;
    }
}
