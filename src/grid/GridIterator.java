package grid;

import utility.ResettableIterator;

public class GridIterator<T> implements ResettableIterator<T> {

  private final T[][][] grid;
  private final int iGridLength;
  private final int jGridLength;
  private final int kGridLength;

  private int i;
  private int j;
  private int k;

  public GridIterator(T[][][] grid, int iGridLength, int jGridLength,
      int kGridLength) {
    this.grid = grid;
    this.iGridLength = iGridLength;
    this.jGridLength = jGridLength;
    this.kGridLength = kGridLength;
    reset();
  }

  @Override
  public boolean hasNext() {
    return i < iGridLength;
  }

  @Override
  public T next() {
    // calculate place in grid and get value
    T retVal = grid[i][j][k];

    // iterate until have reached next good value (at least once)
    increment();

    return retVal;
  }

  private void increment() {
    k = (k + 1) % kGridLength;
    if (k == 0) {
      j = (j + 1) % jGridLength;
      if (j == 0) {
        i++; // don't reset to zero -- use to tell when we're done
      }
    }
  }

  public int getNextI() {
    return i;
  }

  public int getNextJ() {
    return j;
  }

  public int getNextK() {
    return k;
  }

  @Override
  public void reset() {
    i = 0;
    j = 0;
    k = 0;
  }

}
