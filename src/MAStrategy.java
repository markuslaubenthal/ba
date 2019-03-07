import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

class MedialAxisStrategy implements TextStrategy{


  public MAStrategy(){
  }


  public void drawText(VertexPolygon originalPoly, Pane textLayer){

    ArrayList<MAVertex> outline = convertOutline(originalPoly.getOutline());
    int n = outline.size();
    protected PriorityQueue<MALineSegment> Q = new PriorityQueue<>(11, new MALineSegmentComparator());
    protected Stack<MALineSegment> S = new Stack<>();
    protected ArrayList<MALineSegment> L = new ArrayList<>();
    fillPriorityQueue(Q,outline);
    for(int j = 0; j < n-3; j++) {
      MALineSegment uv = Q.poll();
      S.push(uv);
      MAVertex w = u.getPred();
      MAVertex t = v.getSucc();
      MAVertex vnj = findIntersection(w,u,v,t);

    }



  }

  public ArrayList<MAVertex> convertOutline(ArrayList<Vertex> outline) {

    // Convert outline into MAVertecies and set pred and succ

    int n = outline.size();
    ArrayList<MAVertex> outlineMA = new ArrayList<MAVertex>();

    for(int i = 0; i < n; i++) {

      Vertex v = outline.get(i);
      MAVertex newV = new MAVertex(v.x,v.y);
      newV.setPred( outline.get( (i - 1) % n ) );
      newV.setSucc( outline.get( (i + 1) % n ) );

    }

    return outlineMA;

  }

  public void fillPriorityQueue(PriorityQueue<MALineSegment> q, ArrayList<MAVertex> outline) {

    for(MAVertex v : outline) {
      q.add(new MALineSegment(v, v.getSucc()));
    }

  }

  public MAVertex findIntersection(MAVertex w, MAVertex u, MAVertex v, MAVertex t) {

    MAVertex a = w.add(u.sub(w).mult(1000/u.sub(w).mag()));
    MAVertex b = w.add(u.sub(w).mult(-1000/u.sub(w).mag()));
    MAVertex c = v.add(t.sub(v).mult(1000/t.sub(v).mag()));
    MAVertex d = v.add(t.sub(v).mult(-1000/t.sub(v).mag()));
    MALineSegment l1 = new MALineSegment(a,b);
    MALineSegment l2 = new MALineSegment(c,d);
    MAVertex intersection = new MAVertex(0,0);
    l1.getLineIntersection(l2, intersection);
    return intersection;

  }

}
