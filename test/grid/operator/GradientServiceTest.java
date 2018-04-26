package grid.operator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static grid.axis.Dimension.X;
import static grid.axis.Dimension.Y;
import static grid.axis.Dimension.Z;
import helpers.TestUtilities;
import io.log.Log;

import java.util.EnumMap;

import grid.Grid;
import grid.axis.Axes;
import grid.axis.Axis;
import grid.axis.Dimension;
import grid.mask.EllipsoidRadius;
import grid.mask.Mask;
import grid.mask.MaskService;
import grid.mask.MaskedGridIterator;

import org.junit.Test;

@SuppressWarnings("unused")
public class GradientServiceTest {

  @Test
  public void testGradientCalculation() {
    // construct general axes
    EnumMap<Dimension, Axis> axes = new EnumMap<Dimension, Axis>(
        Dimension.class);
    int min = 0;
    int delta = 1;
    int numTicks = 100;
    axes.put(X, new Axis(X, min, delta, numTicks));
    axes.put(Y, new Axis(Y, min, delta, numTicks));
    axes.put(Z, new Axis(Z, min, delta, numTicks));

    // construct mask to use
    int mLen = 5;
    boolean maskGrid[][][] = new boolean[mLen][mLen][mLen];
    for (int i = 0; i < mLen; i++) {
      for (int j = 0; j < mLen; j++) {
        for (int k = 0; k < mLen; k++) {
          maskGrid[i][j][k] = true;
        }
      }
    }
    Mask trueMask = new Mask(maskGrid, mLen/2, mLen/2, mLen/2, mLen, mLen, mLen);

    // make DoG grid iterator
    Grid<Double[]> DoGGrid = new MaskService(mock(Log.class))
        .createDerivativeOfGaussianFilterGrid(new Axes(axes), new EllipsoidRadius(mLen/2));    
    MaskedGridIterator<Double[]> DoG = new MaskedGridIterator<Double[]>(DoGGrid, trueMask);
    DoG.resetAndChangePosition(mLen/2, mLen/2, mLen/2);
    
    // make grid iterator from mathematica data
    Double[][][] interfaceGrid = TestUtilities.getInterfaceVoxet();
    
    Grid<Double> grid = new Grid<Double>(interfaceGrid, interfaceGrid, mLen, mLen, mLen);
    MaskedGridIterator<Double> gridIt = new MaskedGridIterator<Double>(grid, trueMask);
    gridIt.resetAndChangePosition(mLen / 2, mLen / 2, mLen / 2);
    
    // calculate and test gradient
    GradientService gradService = new GradientService();
    double[] gradient = gradService.calculateGradient(gridIt, DoG);
    
    assertThat(gradient[0], is(closeTo(-0.00323457 * Math.pow(2 * Math.PI, 1.5), 1e-5)));
    assertThat(gradient[1], is(closeTo(+0.00762853 * Math.pow(2 * Math.PI, 1.5), 1e-5)));
    assertThat(gradient[2], is(closeTo(-0.0445344 * Math.pow(2 * Math.PI, 1.5), 1e-5)));
  }

}
