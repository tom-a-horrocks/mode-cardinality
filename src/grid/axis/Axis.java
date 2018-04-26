package grid.axis;

public class Axis {

  private static final double TICK_CALCULATION_EPS = 0.01;
  private Dimension dimension;
  private double min;
  private double delta;
  private int numTicks;

  public Axis(Dimension dimension, double min, double delta, int numTicks) {
    this.dimension = dimension;
    this.min = min;
    this.delta = delta;
    this.numTicks = numTicks;
  }

  public Dimension getDimension() {
    return dimension;
  }

  public int coordinateToTick(double coord) {
    double doubleTick = (coord - min) / delta;
    int roundedTick = (int) Math.round(doubleTick);
    if (Math.abs(doubleTick - roundedTick) > TICK_CALCULATION_EPS) {
      throw new RuntimeException("Could not find tick for " + dimension
          + " coordinate value " + coord);
    }
    return roundedTick;
  }

  public double tickToCoordindate(int i) {
    return min + delta * i;
  }

  public int getNumTicks() {
    return numTicks;
  }

  public int numTicksCoveredByLength(double length) {
    // round down to closest grid (not up)
    // +1 because origin always in there.
    return (int) (length / delta) + 1;
  }

  @Override
  public String toString() {
    double max = min + delta * (numTicks - 1);
    String s = dimension + "\n";
    s += "\tmin          : " + min + "\n";
    s += "\tmax          : " + max + "\n";
    s += "\tdelta        : " + delta + "\n";
    s += "\tticks       : " + numTicks;
    return s;
  }

  public double ticksToLength(int numTicks) {
    return numTicks * delta;
  }
  
  public double getDelta() {
    return delta;
  }

}
