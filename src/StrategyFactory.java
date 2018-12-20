
class StrategyFactory {

  public static final String Scan = "Scan Strategy";
  public static final String Simple = "Simple Strategy";
  public static final String Default = "Default";

  public static TextStrategy getStrategy(String name) {
    switch(name) {
      case Scan:
        return new ScanStrategy();
      case Simple:
        return new SimpleStrategy();
      default:
        return new SimpleStrategy();
    }
  }

  public static String getName(TextStrategy strategy) {
    if(strategy instanceof ScanStrategy) return Scan;
    if(strategy instanceof SimpleStrategy) return Simple;
    return Default;
  }
}
