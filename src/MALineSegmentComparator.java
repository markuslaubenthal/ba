import java.util.Comparator;
import java.lang.Math;
/**
 * Implementierung einer Vergleichsfunktion für MAVertices bezüglich Ihrers Abstandes
 * von dem Schnittpunkt der anliegenden Winkelhalbierenden (Bisectoren) in der Priority Queue
 */

public class MALineSegmentComparator implements Comparator<Vertex>{
  public int compare(MALineSegment l1, MALineSegment l2)
    {
      return (int) Math.signum(l2.r - l1.r);
    }
}
