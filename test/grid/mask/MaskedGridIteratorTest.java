package grid.mask;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

import grid.Grid;
import grid.GridIterator;

public class MaskedGridIteratorTest {
  
  @Test
  public void fullSizeGridIteratorFromMaskWorksWithOddGridLength() {
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), null, 3, 3, 3);
    GridIterator<Double> doubleGridIterator = grid.fullGridIterator();
    int n = 0;
    while (doubleGridIterator.hasNext()) {
      assertThat(doubleGridIterator.next(), is((double) n));
      n++;
    }
    assertThat(n, is(27));
  }
  
  @Test
  public void fullSizeGridIteratorFromMaskWorksWithEvenGridLength() {
    Grid<Double> grid = new Grid<Double>(incrementingArray(4, 4, 4), null, 4, 4, 4);
    GridIterator<Double> doubleGridIterator = grid.fullGridIterator();
    int n = 0;
    while (doubleGridIterator.hasNext()) {
      assertThat(doubleGridIterator.next(), is((double) n));
      n++;
    }
    assertThat(n, is(64));
  }

  @Test
  public void retrievesAllPointsInTrueMaskWithinGrid() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), null, 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(1, 1, 1, grid, mask);
    int n = 0;
    while (maskedGridIterator.hasNext()) {
      assertThat(maskedGridIterator.next(), is((double) n));
      n++;
    }
    assertThat(n, is(27));
  }
  
  @Test
  public void doesNotRetrievePointsThatAreMasked() {
    boolean[][][] bools = constArray(true, 3, 3, 3);
    bools[0][0][0] = false;
    Mask mask = new Mask(bools, 1, 1, 1, 3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), null, 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(1, 1, 1, grid, mask);
    int n = 1; // expect 0 to be missing
    while (maskedGridIterator.hasNext()) {
      assertThat(maskedGridIterator.next(), is((double) n));
      n++;
    }
    assertThat(n, is(27));
  }
  
  @Test
  public void doesNotRetrievePointsThatAreOutsideGridBoundaries() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), null, 3, 3, 3);
    
    // set to iterate at (0,0,0) instead of centre
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(0, 0, 0, grid, mask);
    // expect up to (1,1,1) only.
    int n = 0;
    double[] expecteds = new double[] {0, 1, 3, 4, 9, 10, 12, 13};
    while (maskedGridIterator.hasNext()) {
      assertThat(maskedGridIterator.next(), is(expecteds[n]));
      n++;
    }
    assertThat(n, is(8));
  }
  
  @Test
  public void doesNotRetrievePointsThatAreNullsInTheUnderlyingGrid() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Double[][][] incrementingArray = incrementingArray(3, 3, 3);
    incrementingArray[0][0][0] = null;
    Grid<Double> grid = new Grid<Double>(incrementingArray, incrementingArray, 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(1, 1, 1, grid, mask);
    int n = 1; // expect 0 to be missing
    while (maskedGridIterator.hasNext()) {
      Double next = maskedGridIterator.next();
      assertThat(next, is((double) n));
      n++;
    }
    assertThat(n, is(27));    
  }
  
  @Test
  public void whenFillingGridIteratorLooksOutOfBounds() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), incrementingArray(3, 3, 3), 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(0, 0, 0, grid, mask);
    maskedGridIterator.setFillingAndReset(true);
    
    // expect to visit at (-1, -1, -1) first
    assertThat(maskedGridIterator.getNextI(), is(-1));
    assertThat(maskedGridIterator.getNextJ(), is(-1));
    assertThat(maskedGridIterator.getNextK(), is(-1));
    
    // then to (-1, -1, 0)
    maskedGridIterator.next();
    assertThat(maskedGridIterator.getNextI(), is(-1));
    assertThat(maskedGridIterator.getNextJ(), is(-1));
    assertThat(maskedGridIterator.getNextK(), is(0));
  }
  
  @Test
  public void whenFillingGridIteratorOnlyLooksWithinMask() {
    boolean[][][] innerMask = constArray(true, 3, 3, 3);
    innerMask[0][0][0] = false;
    Mask mask = new Mask(innerMask, 1, 1, 1, 3, 3, 3);
    
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), incrementingArray(3, 3, 3), 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(0, 0, 0, grid, mask);
    maskedGridIterator.setFillingAndReset(true);
    
    // (-1, -1, -1) is masked out, so should start at (-1, -1, 0)
    assertThat(maskedGridIterator.getNextI(), is(-1));
    assertThat(maskedGridIterator.getNextJ(), is(-1));
    assertThat(maskedGridIterator.getNextK(), is(0));
  }
  
  @Test
  public void whenFillingTheClosestGridCornerValueIsTaken() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Double[][][] incrementingArray = incrementingArray(3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray, incrementingArray, 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(0, 0, 0, grid, mask);
    maskedGridIterator.setFillingAndReset(true);
    
    // (-1, -1, -1) should be filled with value from (0, 0, 0)
    assertThat(maskedGridIterator.next(), is(0d));
    
    // (-1, -1, 0) should be filled with value from (0, 0, 0)
    assertThat(maskedGridIterator.next(), is(0d));
    
    // (-1, -1, 1) should be filled with value from (0, 0, 1)
    assertThat(maskedGridIterator.next(), is(1d));
    
    // change centre to (1, 2, 2)
    maskedGridIterator.resetAndChangePosition(1, 2, 2); // now pointing at (0, 1, 1)  
    maskedGridIterator.next(); // now pointing at (0, 1, 2)
    maskedGridIterator.next(); // now pointing at (0, 1, 3)
    
    // (0, 1, 3) should be filled with value from (0, 1, 2)
    assertThat(maskedGridIterator.next(), is(incrementingArray[0][1][2]));
  }
  
  @Test
  public void testCoordinateProjection() {
    Mask mask = new Mask(constArray(true, 3, 3, 3), 1, 1, 1, 3, 3, 3);
    Grid<Double> grid = new Grid<Double>(incrementingArray(3, 3, 3), incrementingArray(3, 3, 3), 3, 3, 3);
    
    MaskedGridIterator<Double> maskedGridIterator = new MaskedGridIterator<Double>(0, 0, 0, grid, mask);
    maskedGridIterator.setFillingAndReset(true);
    
    // (0,0,0) projects to 0
    double[] grad = new double[] {1, 2, 3};
    double projectedCoord = maskedGridIterator.getProjectedNextMaskCoordinate(grad, new double[] {1,1,1});
    assertThat(projectedCoord, is(closeTo(0, 1e-5)));
    
    // (0,0,1) projects to 3
    maskedGridIterator.next();
    projectedCoord = maskedGridIterator.getProjectedNextMaskCoordinate(grad, new double[] {1,1,1});
    assertThat(projectedCoord, is(closeTo(3, 1e-5)));
  }

  private Double[][][] incrementingArray(int iLen, int jLen, int kLen) {
    Double[][][] ints = new Double[iLen][jLen][kLen];
    int n = 0;
    for (int i = 0; i < iLen; i++) {
      for (int j = 0; j < jLen; j++) {
        for (int k = 0; k < kLen; k++) {
          ints[i][j][k] = (double) n;
          n++;
        }
      }
    }
    return ints;
  }

  private boolean[][][] constArray(boolean fill, int iLen, int jLen, int kLen) {
    boolean[][][] trues = new boolean[iLen][jLen][kLen];
    for (int i = 0; i < iLen; i++) {
      for (int j = 0; j < jLen; j++) {
        for (int k = 0; k < kLen; k++) {
          trues[i][j][k] = fill;
        }
      }
    }
    return trues;
  }

}
