import java.util.Comparator;

class VerticalTrapezoidYComparator implements Comparator<VerticalTrapezoid> {
    @Override
    public int compare(VerticalTrapezoid a, VerticalTrapezoid b) {
      return a.left.start.y < b.left.start.y ? -1 : 1;
    }
}
