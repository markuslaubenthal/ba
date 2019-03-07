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
    for(int j = 0; j < n-3; j++) {

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
      Line l1 = new Line(wvnj.start.x,wvnj.start.y,wvnj.end.x,wvnj.end.y);
      l1.setStroke(Color.color(1,0,0,0.3));
      textLayer.getChildren().add(l1);
      wvnj.calcCandR(textLayer);
      w.setNextEdge(wvnj);
      Q.add(wvnj);
      MALineSegment vnjt = new MALineSegment(vnj,t);
      Line l2 = new Line(vnjt.start.x,vnjt.start.y,vnjt.end.x,vnjt.end.y);
      l2.setStroke(Color.color(1,0,0,0.3));
      textLayer.getChildren().add(l2);
      vnjt.calcCandR(textLayer);
      vnj.setNextEdge(vnjt);
      Q.add(vnjt);
    }
    for(MALineSegment e : Q) {
      Line l = new Line(e.start.x,e.start.y,e.end.x,e.end.y);
      textLayer.getChildren().add(l);
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
      l.calcCandR(textLayer);
      v.setNextEdge(l);
      Q.add(l);
    }

  }

  public MAVertex findIntersection(MAVertex w, MAVertex u, MAVertex v, MAVertex t) {
    //needs better analytical solution interseciton might me far away.
    MAVertex a = w.add(u.sub(w).mult(4000/u.sub(w).mag()));
    MAVertex b = w.add(u.sub(w).mult(-4000/u.sub(w).mag()));
    MAVertex c = v.add(t.sub(v).mult(4000/t.sub(v).mag()));
    MAVertex d = v.add(t.sub(v).mult(-4000/t.sub(v).mag()));
    MALineSegment l1 = new MALineSegment(a,b);
    MALineSegment l2 = new MALineSegment(c,d);
    MAVertex intersection = new MAVertex(0,0);
    l1.getLineIntersection(l2, intersection);
    return intersection;

  }

}
