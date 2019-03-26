import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Random;

class Test {
  public static void main(String[] args) {
    polygonTest();
    jsonTest();
    hyphenatorTest();
    convexHullTest();
    rotatingCalipersTest();
    sortedTrapezoidsTest();
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
      double offset =  t * 20.0;
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
    ProxyVerticalTrapezoidVertex pv = new ProxyVerticalTrapezoidVertex(v);
    ProxyVerticalTrapezoidVertex proxy = tree.floor(pv);

    System.out.println("ID: " + randomInt + ": " + proxy.t.left.start);


  }

}
