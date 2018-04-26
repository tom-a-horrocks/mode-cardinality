package grid;

import grid.mask.Mask;
import grid.mask.MaskedGridIterator;
import grid.operator.CellOperator;
import io.log.Log;

import java.lang.reflect.Array;

public class Grid<T> {
  private final T[][][] grid;
  private final T[][][] filled;
  private final int iLength;
  private final int jLength;
  private final int kLength;
  private final int size;

  public Grid(T[][][] grid, T[][][] fills, int iLength, int jLength, int kLength) {
    this.grid = grid;
    this.filled = fills;
    this.iLength = iLength;
    this.jLength = jLength;
    this.kLength = kLength;
    this.size = iLength * jLength * kLength;
  }

  @Override
  public String toString() {
    int emptyCells = 0;
    int nonEmptyCells = 0;
    int totalCells = 0;
    GridIterator<T> it = fullGridIterator();
    while (it.hasNext()) {
      int i = it.getNextI();
      int j = it.getNextJ();
      int k = it.getNextK();
      it.next();
      totalCells++;
      if (!isValid(i, j, k)) {
        emptyCells++;
      } else {
        nonEmptyCells++;
      }
    }

    String s = "";
    s += "Total cells    : " + totalCells + System.lineSeparator();
    s += "Non-empty cells: " + nonEmptyCells + System.lineSeparator();
    s += "Empty cells    : " + emptyCells;

    return s;
  }

  public T at(int i, int j, int k) {
    return grid[i][j][k];
  }

  /**
   * Iterates over the full grid, including missing values, in increasing order
   * of (i,j,k).
   * 
   * @return The griditerator to iterate across the full grid.
   */
  public GridIterator<T> fullGridIterator() {
    return new GridIterator<T>(grid, iLength, jLength, kLength);
  }

  @SuppressWarnings("unchecked")
  private <E> E[][][] createNullGridArray(Class<E> elementClass) {
    return (E[][][]) Array.newInstance(elementClass, iLength, jLength, kLength);
  }

  public boolean isInBounds(int i, int j, int k) {
    return i >= 0 && j >= 0 && k >= 0 && i < iLength && j < jLength
        && k < kLength;
  }

  public boolean isValid(int i, int j, int k) {
    return grid[i][j][k] != null;
  }
  
  public <OUT_TYPE, ARG_TYPE> Grid<OUT_TYPE> applyPerCell(CellOperator<T, OUT_TYPE, ARG_TYPE> cellOperator, ARG_TYPE args, Mask mask, Log log) {
    GridIterator<T> it = fullGridIterator();
    OUT_TYPE[][][] resultGrid = createNullGridArray(cellOperator.getReturnCellType());
    
    double nCompleted = 0;
    double nextFracCheckpoint = 0.1;
    long startTime = System.currentTimeMillis();
    
    // Use same masked grid iterator for efficiency
    MaskedGridIterator<T> maskedGridIterator = new MaskedGridIterator<T>(this, mask);
    while (it.hasNext()) {
      int i = it.getNextI();
      int j = it.getNextJ();
      int k = it.getNextK();
      it.next();
      
      maskedGridIterator.resetAndChangePosition(i, j, k);
      
      resultGrid[i][j][k] = cellOperator.execute(maskedGridIterator, args);
      
      nCompleted++;
      if (nCompleted / size > nextFracCheckpoint) {
        long timeSpentSecs = (System.currentTimeMillis() - startTime) / 1000;
        long secsRemaining = Math.round((1 - nextFracCheckpoint) / nextFracCheckpoint * timeSpentSecs);
        log.printInformationMessage("Completed " + Math.round(100 * nextFracCheckpoint) + "% in " + timeSpentSecs + " seconds, est. " + secsRemaining + " seconds remaining");
        nextFracCheckpoint += 0.1;
      }
    }
    
    return new Grid<OUT_TYPE>(resultGrid, null, iLength, jLength, kLength);
  }

  public T getFillValue(int i, int j, int k) {
    // first clip indexes to grid boundary
    int iFill = clip(i, 0, iLength - 1);
    int jFill = clip(j, 0, jLength - 1);
    int kFill = clip(k, 0, kLength - 1);

    // return pre-computed fill value
    return filled[iFill][jFill][kFill];
  }
  
  private int clip(int val, int min, int max) {
    if (val < min) {
      return min;
    }
    if (val > max) {
      return max;
    }
    return val;
  }


}