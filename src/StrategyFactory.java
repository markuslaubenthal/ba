
class StrategyFactory {

  public static final String Scan = "Scan Strategy";
  public static final String Simple = "Simple Strategy";
  public static final String Graph = "Graph Strategy";
  public static final String MedialAxisDF = "Medial Axis DF Strategy";
  public static final String CenterX = "Center X Strategy";
  public static final String MedialAxis = "Medial Axis Strategy";
  public static final String Default = "Default";
  public static final String GraphSplit = "Graph Split Strategy";
  public static final String Convex = "Convex";
  public static final String Concave = "Concave";

  public static TextStrategy getStrategy(String name) {
    switch(name) {
      case Scan:
        return new ScanStrategy();
      case MedialAxis:
        return new MAStrategy();
      case MedialAxisDF:
        return new MAStrategyDF();
      case CenterX:
        return new CenterXStrategy();
      case Simple:
        return new SimpleStrategy();
      case Graph:
        return new GraphStrategy();
      case GraphSplit:
        return new GraphSplitStrategy();
      case Convex:
        return new ConvexStrategy();
      case Concave:
        return new ConcaveStrategy();
      default:
        return new CenterXStrategy();
    }
  }

  public static String getName(TextStrategy strategy) {
    if(strategy instanceof ScanStrategy) return Scan;
    if(strategy instanceof MAStrategy) return MedialAxis;
    if(strategy instanceof MAStrategyDF) return MedialAxisDF;
    if(strategy instanceof CenterXStrategy) return CenterX;
    if(strategy instanceof SimpleStrategy) return Simple;
    if(strategy instanceof GraphStrategy) return Graph;
    if(strategy instanceof GraphSplitStrategy) return GraphSplit;
    return Default;
  }
}
