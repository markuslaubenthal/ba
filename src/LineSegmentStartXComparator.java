import java.util.Comparator;
import java.lang.Math;
/**
 * Implementierung einer Vergleichsfunktion für MAVertices bezüglich Ihrers Abstandes
 * von dem Schnittpunkt der anliegenden Winkelhalbierenden (Bisectoren) in der Priority Queue
 */

public class LineSegmentStartXComparator implements Comparator<LineSegment>{
  public int compare(LineSegment l1, LineSegment l2)
    {
      return (int) Math.signum(l1.start.x - l2.start.x);
    }
}
