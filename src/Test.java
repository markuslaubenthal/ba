import java.util.ArrayList;
import java.util.List;


class Test {
  public static void main(String[] args) {
    polygonTest();
    jsonTest();
    hyphenatorTest();
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
    List<String> output = h.hyphenate("DEUTSCHLAND");
    System.out.println(output);
  }
}
