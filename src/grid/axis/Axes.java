package grid.axis;

import static grid.axis.Dimension.*;
import grid.Grid;
import io.log.Log;

import java.util.Arrays;
import java.util.EnumMap;

public class Axes {

  private final EnumMap<Dimension, Axis> axes;

  public Axes(EnumMap<Dimension, Axis> axes) {
    this.axes = axes;
  }

  public void setValueInArray(double a, double x, double y, double z,
      Double[][][] array) {
    int i = axes.get(X).coordinateToTick(x);
    int j = axes.get(Y).coordinateToTick(y);
    int k = axes.get(Z).coordinateToTick(z);
    array[i][j][k] = a;
  }

  public Double[][][] createNullArray(Log log) {
    // create empty grid
    int xTicks = getNumTicks(X);
    int yTicks = getNumTicks(Y);
    int zTicks = getNumTicks(Z);
    log.printInformationMessage("Creating array of size " + xTicks + "x" + yTicks + "x" + zTicks + " (" + (4 * xTicks*yTicks*zTicks / (1024 * 1024)) + " MB)");
    Double[][][] grid = new Double[xTicks][yTicks][zTicks];
    for (int i = 0; i < xTicks; i++) {
      for (int j = 0; j < yTicks; j++) {
        for (int k = 0; k < zTicks; k++) {
          grid[i][j][k] = null;
        }
      }
    }
    return grid;
  }

  // lower-bounded (i.e. maximum no. ticks actually reached).
  public int numTicksCoveredByLength(double length, Dimension d) {
    return axes.get(d).numTicksCoveredByLength(length);
  }

  // for exporting
  public double tickToCoordinate(int tick, Dimension d) {
    return axes.get(d).tickToCoordindate(tick);
  }

  @Override
  public String toString() {
    String s = "";
    for (Dimension dim : Dimension.values()) {
      s += axes.get(dim).toString() + System.lineSeparator();
    }
    s += "normalised scales: " + Arrays.toString(getNormalisedScales());
    return s;
  }

  private int getNumTicks(Dimension d) {
    return axes.get(d).getNumTicks();
  }

  public <T> Grid<T> createGrid(T[][][] array, T[][][] fillVolume) {
    return new Grid<T>(array, fillVolume, getNumTicks(X), getNumTicks(Y), getNumTicks(Z));
  }

  public double squaredDistance(int i1, int i2, Dimension d) {
    double dist = axes.get(d).ticksToLength(i2 - i1);
    return dist * dist;
  }
  
  public double distance(int i1, int i2, Dimension d) {
    return axes.get(d).ticksToLength(i2 - i1);
  }

  // gets the scales normalised to be max at 1
  public double[] getNormalisedScales() {
    double xScale = axes.get(X).getDelta();
    double yScale = axes.get(Y).getDelta();
    double zScale = axes.get(Z).getDelta();
    double[] scales = { xScale, yScale, zScale };
    divideByMax(scales);
    return scales;
  }

  private void divideByMax(double[] scales) {
    double max = Double.NEGATIVE_INFINITY;
    for (double scale : scales) {
      max = Math.max(max, scale);
    }
    for (int i = 0; i < scales.length; i++) {
      scales[i] /= max;
    }
  }

}
