import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import java.util.Collections;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.util.Collections;


class NewScanStrategy implements TextStrategy{
  double fontsize;
  Pane textLayer;
  String text;


  public NewScanStrategy(){

  }

  public void drawText(VertexPolygon poly, Pane textLayer){

    
    /*
    polyArea = poly.getAreaSize();

    double approximationscore = 0;
    double threshold = 0.5;

    double biggestFonstScale = 2;
    double smallestFonstScale = 0.5;

    //IF TOO NARROW : create sub Polygons and try again
    ArrayList<VertexPolygon> letterBoxes = new ArrayList<>();
    ArrayList<VertexPolygon> polygonList = new ArrayList<>();
    polygonList.add(poly);

    int linecount = 1;

    while(approximationscore < threshold){
      letterBoxes = new ArrayList<>();

      for(VertexPolygon subpoly : polygonList){
        double fontsize = calculateFontsize(subpoly); // mal schauen
        Grid G = new Grid(subpoly, fontsize * 4); // markus
        ArrayList<LineSegment> path = G.findBiggestPath(); // markus
        letterBoxes.addAll(fitRectanglesToLinesgegments(path, fontsize, subpoly, biggestFonstScale, smallestFonstScale)); // lenny
      }

      double letterBoxesArea = calculateBoxesArea(letterBoxes);
      approximationscore = letterBoxesArea / polyArea;

      if(approximationscore < threshold) {
        linecount++;
        polygonList = slicePolygon(poly, linecount); // braucht mehr kopf später
        sliceText(polygonList, text); // sinn und verstand
      }

      if(linecount > 20){
        break;
      }

    }

    for(VertexPolygon letterBox : letterBoxes){
      drawLetter(letterBox); //lenny
    }
    */
  }

  public void drawLetter(VertexPolygon letterBox){

  }

}
