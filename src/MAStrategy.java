import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import javafx.scene.shape.Line;
import java.lang.Math;
import javafx.scene.paint.Color;

class MAStrategy implements TextStrategy{


  public MAStrategy() {}


  public void drawText(VertexPolygon originalPoly, Pane textLayer){

    ArrayList<MAVertex> outline = convertOutline(originalPoly.getOutline());
    int n = outline.size();
    PriorityQueue<MALineSegment> Q = new PriorityQueue<>(11, new MALineSegmentComparator());
    Stack<MALineSegment> S = new Stack<>();
    ArrayList<MALineSegment> L = new ArrayList<>();
    fillPriorityQueue(Q,outline, textLayer);
    int j = 0;
    while(j < n-3) {

      MALineSegment uv = Q.poll();
      S.push(uv);
      MAVertex u = uv.start;
      MAVertex v = uv.end;
      MAVertex w = u.getPred();
      MAVertex t = v.getSucc();
      MAVertex vnj = findIntersection(w,u,v,t);
      w.setSucc(vnj);
      t.setPred(vnj);
      vnj.setPred(w);
      vnj.setSucc(t);
      //remove old edges from Q and add the new constructed ones.
      Q.remove(w.getNextEdge());
      Q.remove(u.getNextEdge());
      Q.remove(v.getNextEdge());
      MALineSegment wvnj = new MALineSegment(w,vnj);
      wvnj.calcCandR();
      w.setNextEdge(wvnj);
      Q.add(wvnj);
      MALineSegment vnjt = new MALineSegment(vnj,t);
      vnjt.calcCandR();
      vnj.setNextEdge(vnjt);
      Q.add(vnjt);

      Line l1 = new Line(wvnj.start.x,wvnj.start.y,wvnj.end.x,wvnj.end.y);
      l1.setStroke(Color.color(1,0,0,0.3));
      textLayer.getChildren().add(l1);
      Line l2 = new Line(vnjt.start.x,vnjt.start.y,vnjt.end.x,vnjt.end.y);
      l2.setStroke(Color.color(1,0,0,0.3));
      textLayer.getChildren().add(l2);

      j++;

    }

    MALineSegment u1u2 = Q.poll();
    MAVertex u1 = u1u2.start;
    MAVertex u2 = u1u2.end;
    MAVertex u3 = u2.getSucc();
    u1.setParent(u1u2.center);
    u2.setParent(u1u2.center);
    u3.setParent(u1u2.center);

    while(j > 0) {

      MALineSegment uv = S.pop();
      MAVertex u = uv.start;
      MAVertex v = uv.end;
      MAVertex w = u.getPred();
      MAVertex t = v.getSucc();
      MAVertex z = w.getSucc();
      MAVertex parent = z.getParent();
      MALineSegment centerParent = new MALineSegment(uv.center, parent);
      L.add(centerParent);
      w.setSucc(u);
      t.setPred(v);
      z.setParent(null);
      u.setParent(uv.center);
      v.setParent(uv.center);
      j = j - 1;

    }

    for(MAVertex x : outline) {
      MALineSegment vertexParent = new MALineSegment(x, x.getParent());
      L.add(vertexParent);
    }

    for(MALineSegment l : L) {
      Line l1 = new Line(l.start.x,l.start.y,l.end.x,l.end.y);
      l1.setStroke(Color.color(0,1,0,0.3));
      textLayer.getChildren().add(l1);
    }





  }

  public ArrayList<MAVertex> convertOutline(ArrayList<Vertex> outline) {

    // Convert outline into MAVertecies and set pred and succ

    int n = outline.size();
    ArrayList<MAVertex> outlineMA = new ArrayList<MAVertex>();

    for(Vertex v : outline) {
      outlineMA.add(new MAVertex(v.x, v.y));
    }

    for(int i = 0; i < n; i++) {
      MAVertex v = outlineMA.get(i);
      v.setPred(outlineMA.get( Math.floorMod(i - 1, n) ));
      v.setSucc(outlineMA.get( Math.floorMod(i + 1, n) ));
    }

    return outlineMA;

  }

  public void fillPriorityQueue(PriorityQueue<MALineSegment> Q, ArrayList<MAVertex> outline, Pane textLayer) {

    for(MAVertex v : outline) {
      MALineSegment l = new MALineSegment(v, v.getSucc());
      l.calcCandR();
      v.setNextEdge(l);
      Q.add(l);
    }

  }

  public MAVertex findIntersection(MAVertex w, MAVertex u, MAVertex v, MAVertex t) {
    //needs better analytical solution interseciton might me far away.
    MALineSegment l1 = new MALineSegment(w,u);
    MALineSegment l2 = new MALineSegment(v,t);
    MAVertex intersection = new MAVertex(0,0);
    l1.getLineIntersection(l2, intersection);
    return intersection;

  }

}
