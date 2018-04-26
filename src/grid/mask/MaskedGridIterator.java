package grid.mask;

import utility.ResettableIterator;
import grid.Grid;

public class MaskedGridIterator<T> implements ResettableIterator<T> {

  private int iGridCenter;
  private int jGridCenter;
  private int kGridCenter;
  
  private int iMask;
  private int jMask;
  private int kMask;
  
  private boolean fill;

  private final Grid<T> grid;
  private final Mask mask;

  // iterate with i,j,k at the centre
  public MaskedGridIterator(int iGridCenter, int jGridCenter, int kGridCenter,
      Grid<T> grid, Mask mask) {
    this.iGridCenter = iGridCenter;
    this.jGridCenter = jGridCenter;
    this.kGridCenter = kGridCenter;

    this.grid = grid;
    this.mask = mask;
    
    this.fill = false;
    
    reset();
  }

  public MaskedGridIterator(Grid<T> grid, Mask mask) {
    this.iGridCenter = 0;
    this.jGridCenter = 0;
    this.kGridCenter = 0;
    
    this.grid = grid;
    this.mask = mask;
    
    this.fill = false;
    
    reset();
  }
  
  public void resetAndChangePosition(int iGridCenter, int jGridCenter, int kGridCenter) {
    this.iGridCenter = iGridCenter;
    this.jGridCenter = jGridCenter;
    this.kGridCenter = kGridCenter;
    reset();
  }

  @Override
  public void reset() {
    iMask = 0;
    jMask = 0;
    kMask = 0;

    // if current value not good, iterate until good.
    while (!iteratedEntireMask() && !currentValueValid()) {
      increment();
    }
  }

  @Override
  public boolean hasNext() {
    return !iteratedEntireMask();
  }

  @Override
  public T next() {
    if (iteratedEntireMask()) {
      throw new RuntimeException("Have already iterated over entire grid");
    }

    // calculate place in grid and get value
    int i = getNextI();
    int j = getNextJ();
    int k = getNextK();
    
    T retVal;
    if (fill) {
      retVal = grid.getFillValue(i, j, k);
    } else {
      retVal = grid.at(i, j, k);
    }
    // iterate until have reached next good value (at least once)
    do {
      increment();
    } while (!iteratedEntireMask() && !currentValueValid());
   
    return retVal;
  }
  
  public boolean centreValueIsValid() {
    return grid.isValid(iGridCenter, jGridCenter, kGridCenter);
  }
  
  public T getCentreValue() {
    return grid.at(iGridCenter, jGridCenter, kGridCenter);
  }

  private void increment() {
    kMask = (kMask + 1) % mask.kLength();
    if (kMask == 0) {
      jMask = (jMask + 1) % mask.jLength();
      if (jMask == 0) {
        iMask++; // don't reset to zero -- use to tell when we're done
      }
    }
  }

  private boolean iteratedEntireMask() {
    return iMask >= mask.iLength();
  }

  private boolean currentValueValid() {
    // only ever look for values within mask
    if (!mask.included(iMask, jMask, kMask)) {
      return false;
    }

    // if within mask then we can fill it
    if (fill) {
      return true;
    }
    
    // calculate place in grid
    int i = getNextI();
    int j = getNextJ();
    int k = getNextK();

    // is a valid value if not a NaN
    return grid.isInBounds(i, j, k) && grid.isValid(i, j, k);
  }

  public int getNextI() {
    return mask.iMid() - (mask.iLength() - 1 - iMask) + iGridCenter;
  }

  public int getNextJ() {
    return mask.jMid() - (mask.jLength() - 1 - jMask) + jGridCenter;
  }

  public int getNextK() {
    return mask.kMid() - (mask.kLength() - 1 - kMask) + kGridCenter;
  }
  
  // scales[] are the relative increases in each dimension normalised by the largest.
  // e.g., for dx = 30m, dy = 30m, dz = 15m,
  // expect scales = [1.0,1.0,0.5]
  // important: this only works because the mask indices are zero-based
  // Do this to keep a good hp as 1 (otherwise would be data-dependent)
  public double getProjectedNextMaskCoordinate(double[] gradient, double[] scales) {
    double xProd = iMask * gradient[0] * scales[0];
    double yProd = jMask * gradient[1] * scales[1];
    double zProd = kMask * gradient[2] * scales[2];
    
    return xProd + yProd + zProd;
  }
  
  public void setFillingAndReset(boolean fill) {
    this.fill = fill;
    reset();
  }

}
