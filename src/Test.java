import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;

class Test {
  public static void main(String[] args) {
    // polygonTest();
    // jsonTest();
    // hyphenatorTest();
    // convexHullTest();
    // rotatingCalipersTest();
    // sortedTrapezoidsTest();
    // vertexListTest();
    // orderingTest();
    // intersectionTest();
    // outlineTest();
    areaCalcTest();
  }

  public static void polygonTest() {
    VertexPolygon x = new VertexPolygon();
    x.addVertex(new Vertex(0, 0));
    x.addVertex(new Vertex(3, 20));
    x.addVertex(new Vertex(17, 15));
    x.addVertex(new Vertex(14, 15));
    double area = x.getAreaSize();
    System.out.println("Ergebnnis: " + area);
    System.out.println("Erwartet:  " + "125.0");
  }

  public static void jsonTest() {
    VertexPolygon x = new VertexPolygon();
    x.addVertex(new Vertex(0, 0));
    x.addVertex(new Vertex(3, 20));
    x.addVertex(new Vertex(17, 15));
    x.addVertex(new Vertex(14, 15));

    VertexPolygon y = new VertexPolygon();
    y.addVertex(new Vertex(0 + 20, 0 + 20));
    y.addVertex(new Vertex(3 + 20, 20 + 20));
    y.addVertex(new Vertex(17 + 20, 15 + 20));
    y.addVertex(new Vertex(14 + 20, 15 + 20));

    ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();
    polygonList.add(x);
    polygonList.add(y);

    PolygonWriter writer = new PolygonWriter(polygonList);
    // writer.save("test.txt");
  }

  public static void hyphenatorTest() {
    HyphenGenerator h = new HyphenGenerator();
    System.out.println("Hyphenator created");
    List<String> output = h.hyphenate("Schifffahrtskapitän");
    System.out.println(output);
  }

  public static void convexHullTest() {
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    vertices.add(new Vertex(10, 10));
    vertices.add(new Vertex(20, 20));
    vertices.add(new Vertex(30, 10));
    vertices.add(new Vertex(30, 40));
    vertices.add(new Vertex(20, 30));
    vertices.add(new Vertex(10, 40));
    ArrayList<Vertex> res = ConvexHull.convexHull(vertices);
    System.out.println(res);
  }

  public static void rotatingCalipersTest() {
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    vertices.add(new Vertex(10, 10));
    vertices.add(new Vertex(20, 20));
    vertices.add(new Vertex(30, 10));
    vertices.add(new Vertex(500, 40));
    vertices.add(new Vertex(50, 500));
    vertices.add(new Vertex(10, 40));
    ArrayList<Vertex> res = RotatingCalipersAdapter.getMinimumBoundingRectangle(vertices);
    Vertex orientation = new Vertex(0,0);
    for(int i = 0; i < 2; i++) {
      if(res.get(i).sub(res.get(i + 1)).mag() > orientation.mag()) orientation = res.get(i).sub(res.get(i + 1));
    }
    System.out.println(orientation);
    System.out.println(res);
  }

  public static void sortedTrapezoidsTest() {
    Random r = new Random();
    double c = 20.0;
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    TreeSet<ProxyVerticalTrapezoidVertex> tree =
      new TreeSet<ProxyVerticalTrapezoidVertex>(new VerticalTrapezoidVertexComparator());
    for(int t = 0; t < c; t++) {
      double height = 800.0 / c;
      double offset =  t * height;
      double min = offset;
      double max = offset + height;
      double left1 = min + (max - min) * r.nextDouble();
      double left2 = min + (max - min) * r.nextDouble();
      double right1 = min + (max - min) * r.nextDouble();
      double right2 = min + (max - min) * r.nextDouble();
      Vertex v1 = new Vertex(10, Math.min(left1, left2));
      Vertex v2 = new Vertex(10, Math.max(left1, left2));
      Vertex v3 = new Vertex(20, Math.min(right1, right2));
      Vertex v4 = new Vertex(20, Math.max(right1, right2));

      vertices.add(v3);
      vertices.add(v4);

      LineSegment left = new LineSegment(v1, v2);
      LineSegment top = new LineSegment(v1, v3);
      LineSegment bot = new LineSegment(v2, v4);

      VerticalTrapezoid vt = new VerticalTrapezoid(left, top, bot);
      ProxyVerticalTrapezoidVertex pvt = new ProxyVerticalTrapezoidVertex(vt);
      tree.add(pvt);
    }

    int count = vertices.size();
    int randomInt = (int) (r.nextDouble() * (double) count);
    Vertex v = vertices.get(randomInt);

    double randLeft = 1 + (400 - 1) * r.nextDouble();
    Vertex v2 = new Vertex(15, 100000);

    ProxyVerticalTrapezoidVertex pv = new ProxyVerticalTrapezoidVertex(v);
    ProxyVerticalTrapezoidVertex pv2 = new ProxyVerticalTrapezoidVertex(v2);
    ProxyVerticalTrapezoidVertex proxy = tree.floor(pv2);
    if(tree.contains(pv)) {
      System.out.println("Contains pv");
    } else {
      System.out.println("Not Contains pv");
    }
    if(!tree.contains(pv2)) {
      System.out.println("Not Contains pv2");
    } else {
      System.out.println("Contains pv2");
    }
    System.out.println("ID: " + v + ": " + proxy.t.left.start);
  }

  public static void vertexListTest() {
    VertexList l = new VertexList();
    for(int i = 0; i < 50; i++) {
      l.add(new Vertex(i, i+1));
      System.out.println(i);
      System.out.println(l._getPrev(new Vertex(i, i+1)));
    }
    Vertex v = new Vertex(0,1);
    if(l.contains(v)) System.out.println("VList Contains");
    // System.out.println(vList.get(v));
    System.out.println(Arrays.toString(l.toArray()));
    if(l.contains(l.getNext(v))) System.out.println("VList Contains");
    System.out.println(l.getNext(v));

  }

  public static void orderingTest() {
    Vertex[] v = new Vertex[16];
    for(int i = 1; i < 5; i++) {
      for(int k = 1; k < 5; k++) {
        v[(i - 1) * 4 + (k-1)] = new Vertex(i, k);
      }
    }
    Arrays.sort(v, new VertexXComparator());
    System.out.println(Arrays.toString(v));
  }

  public static void intersectionTest() {
    Vertex v1 = new Vertex(20, 20);
    Vertex v2 = new Vertex(30, 10);
    Vertex v3 = new Vertex(40, 20);
    LineSegment l1 = new LineSegment(v1, v2);
    LineSegment l2 = new LineSegment(v2, v3);
    LineSegment l3 = new LineSegment(v2.x, v2.y, v2.x, 1000);

    Vertex i = new Vertex(-1,-1);
    l1.getLineIntersection(l2, i);
    System.out.println(i);
    l1.getLineIntersection(l3, i);
    System.out.println(i);
  }

  public static void outlineTest() {
    Vertex v = new Vertex(20,20);
    VertexList l = new VertexList();
    l.add(v);
    v.x = 10;
    if(l.contains(v)) System.out.println("contains");
  }

  public static void areaCalcTest() {
    Vertex v1 = new Vertex(50,50);
    Vertex v2 = new Vertex(50,30);
    Vertex v3 = new Vertex(60,45);
    Vertex v4 = new Vertex(70,75);
    Vertex v5 = new Vertex(50,65);
    VerticalTrapezoid t1 = new VerticalTrapezoid(
      new LineSegment(v2, v1),
      new LineSegment(v2, v3),
      new LineSegment(v3, v3),
      new LineSegment(v1, v3)
    );
    VerticalTrapezoid t2 = new VerticalTrapezoid(
      new LineSegment(v1, v5),
      new LineSegment(v1, v4),
      new LineSegment(v4, v4),
      new LineSegment(v5, v4)
    );

    double a1 = t1.bot.slope() + t2.bot.slope();
    double a2 = t1.top.slope() + t2.top.slope() ;
    double b1 = t1.bot.functionOffset() + t2.bot.functionOffset();
    double b2 = t1.top.functionOffset() + t2.top.functionOffset();
    double sweep = 60;
    double s_last = 50;

    double polyArea = t1.area() + t2.area();

    double area = ((a1/2 * Math.pow(sweep,2) + b1*sweep - (a1/2 * Math.pow(s_last,2) + b1 * s_last))
      - (a2/2 * Math.pow(sweep,2) + b2 * sweep - (a2/2 * Math.pow(s_last,2) + b2 * s_last)));

    System.out.println("Polygon: " + polyArea);
    System.out.println("Sweep 50/60: " + area);

    a1 -= t1.bot.slope();
    a2 -= t1.top.slope();
    b1 -= t1.bot.functionOffset();
    b2 -= t1.top.functionOffset();
    sweep = 70;
    s_last = 60;
    area = ((a1/2 * Math.pow(sweep,2) + b1*sweep - (a1/2 * Math.pow(s_last,2) + b1 * s_last))
      - (a2/2 * Math.pow(sweep,2) + b2 * sweep - (a2/2 * Math.pow(s_last,2) + b2 * s_last)));

    System.out.println("Sweep 60/70: " + area);
  }

}
