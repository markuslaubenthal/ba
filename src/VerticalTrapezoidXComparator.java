import java.util.Comparator;

class VerticalTrapezoidXComparator implements Comparator<VerticalTrapezoid> {
    @Override
    public int compare(VerticalTrapezoid a, VerticalTrapezoid b) {
      return a.right.start.x < b.right.start.x ? -1 : 1;
    }
}
