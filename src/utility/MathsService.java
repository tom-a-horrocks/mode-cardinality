package utility;

import io.log.Log;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.inject.Inject;

public class MathsService {

  private final Log log;

  @Inject
  public MathsService(Log log) {
    this.log = log;
  }

  public double min(Iterator<Double> iterator) {
    if (!iterator.hasNext()) {
      return Double.NaN;
    }

    double min = iterator.next();

    while (iterator.hasNext()) {
      double val = iterator.next();
      min = Math.min(min, val);
    }
    return min;
  }

  public double max(Iterator<Double> iterator) {
    if (!iterator.hasNext()) {
      return Double.NaN;
    }

    double max = iterator.next();

    while (iterator.hasNext()) {
      double val = iterator.next();
      max = Math.max(max, val);
    }
    return max;
  }

  public double min(ArrayList<Double> arr) {
    if (arr.isEmpty()) {
      return Double.NaN;
    }

    double min = arr.get(0);

    for (int i = 1; i < arr.size(); i++) {
      min = Math.min(min, arr.get(i));
    }
    return min;
  }

  public double max(ArrayList<Double> arr) {
    if (arr.isEmpty()) {
      return Double.NaN;
    }

    double max = arr.get(0);

    for (int i = 1; i < arr.size(); i++) {
      max = Math.max(max, arr.get(i));
    }
    return max;
  }

  /**
   * Counts the peaks in the given 2D grid. Assumes that the function decreases
   * out of bounds of the grid.
   * 
   * Assumes function has no flat parts (i.e. always increasing or decreasing).
   * 
   * @param pdf
   * @return
   */
  public int countPeaks(double[][] pdf, int d1Length, int d2Length) {
    int nPeaks = 0;
    boolean hasFlatCells = false;

    for (int i = 0; i < d1Length; i++) {
      for (int j = 0; j < d2Length; j++) {
        double center = pdf[i][j];
        // look at surrounding 3x3x3 grid
        boolean isPeak = true;
        int ii = i - 1;
        while (isPeak && ii <= i + 1) {
          // neighbour rows
          int jj = j - 1;
          while (isPeak && jj <= j + 1) {
            // neighbour cols
            if (ii == i && jj == j) {
              // at neighbour
              jj++;
              continue;
            }
            double neighbour = getPaddedValue(pdf, ii, jj, d1Length, d2Length);
            hasFlatCells = hasFlatCells || (center == neighbour);
            isPeak = center > neighbour; // sentinel value
            jj++;
          }
          ii++;
        }
        if (isPeak) {
          nPeaks++;
        }
      }
    }

    if (hasFlatCells) {
      log.printWarningMessage("Encountered flat regions when counting peaks");
    }
    return nPeaks;
  }

  private double getPaddedValue(double[][] pdf, int i, int j, int iLen, int jLen) {
    if (i < 0 || j < 0 || i >= iLen || j >= jLen) {
      return Double.MIN_VALUE;
    }
    return pdf[i][j];
  }

  public Integer countPeaks(double[] pdf, int length) {
    int nPeaks = 0;
    int nFlatCells = 0;

    for (int i = 0; i < length; i++) {
      double center = pdf[i];

      double left = (i == 0) ? Double.MIN_VALUE : pdf[i - 1];
      double right = (i == length - 1) ? Double.MIN_VALUE : pdf[i + 1];

      if (center == left || center == right) {
        nFlatCells++;
        continue;
      }

      if (center > left && center > right) {
        nPeaks++;
      }
    }

    if (nFlatCells > 0) {
      log.printWarningMessage("Encountered " + nFlatCells
          + " flat regions when counting peaks");
    }
    return nPeaks;
  }

}
