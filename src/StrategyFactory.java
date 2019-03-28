
class StrategyFactory {

  public static final String Scan = "Scan Strategy";
  public static final String Simple = "Simple Strategy";
  public static final String Graph = "Graph Strategy";
  public static final String Default = "Default";
  public static final String GraphSplit = "Graph Split Strategy";

  public static TextStrategy getStrategy(String name) {
    switch(name) {
      case Scan:
        return new ScanStrategy();
      case Simple:
        return new SimpleStrategy();
      case Graph:
        return new GraphStrategy();
      case GraphSplit:
        return new GraphSplitStrategy();
      default:
        return new ConvexStrategy();
    }
  }

  public static String getName(TextStrategy strategy) {
    if(strategy instanceof ScanStrategy) return Scan;
    if(strategy instanceof SimpleStrategy) return Simple;
    if(strategy instanceof GraphStrategy) return Graph;
    if(strategy instanceof GraphSplitStrategy) return GraphSplit;
    return Default;
  }
}
