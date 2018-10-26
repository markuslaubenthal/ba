import java.util.ArrayList;

class Test {
  public static void main(String[] args) {
    polygonTest();
    jsonTest();
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

    ArrayList<VertexPolygon> polygonList = new ArrayList<VertexPolygon>();
    polygonList.add(x);

    PolygonWriter writer = new PolygonWriter(polygonList);
    writer.save();

  }
}
