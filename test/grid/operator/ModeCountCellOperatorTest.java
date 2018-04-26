package grid.operator;

import static grid.axis.Dimension.X;
import static grid.axis.Dimension.Y;
import static grid.axis.Dimension.Z;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import helpers.TestUtilities;

import io.log.Log;
import java.util.EnumMap;
import utility.MathsService;
import grid.Grid;
import grid.axis.Axes;
import grid.axis.Axis;
import grid.axis.Dimension;
import grid.mask.EllipsoidRadius;
import grid.mask.Mask;
import grid.mask.MaskService;
import grid.mask.MaskedGridIterator;
import kde.KernelDensityEstimator;
import kde.bw.BandwidthSelector;
import kde.bw.SilvermanBandwidthSelector;
import kde.kernel.GaussianKernel;

@SuppressWarnings("unused")
public class ModeCountCellOperatorTest {
  
  @Mock private GradientService gradientService;
  @Mock private KernelDensityEstimator kde;
  @Mock private MathsService mathsService;
  @Mock private Log log;
  
  @Mock private MaskedGridIterator<Double> gridIt;
  @Mock private MaskedGridIterator<Double[]> DoG;
  
  @InjectMocks
  private ModeCountCellOperator modeCountCellOperator;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void integrationTestOnInterfaceVoxet() {
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
    
    ModeCountCellOperatorArgs args = new ModeCountCellOperatorArgs(
        new SilvermanBandwidthSelector(), 
        new SilvermanBandwidthSelector(),
        20,
        DoG,
        0,
        new double[] {1,1,1});
    ModeCountCellOperator op = new ModeCountCellOperator(
        new KernelDensityEstimator(new GaussianKernel()), 
        new MathsService(log), 
        new GradientService());
    
    int nModes = op.execute(gridIt, args).getNumModes();
    
    assertThat(nModes, is(3));
  }
  
  @Test
  public void integrationTestOnHomogeneousNoisyVoxet() {
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
    Double[][][] interfaceGrid = TestUtilities.getHomogeneousVoxet();
    
    Grid<Double> grid = new Grid<Double>(interfaceGrid, interfaceGrid, mLen, mLen, mLen);
    MaskedGridIterator<Double> gridIt = new MaskedGridIterator<Double>(grid, trueMask);
    gridIt.resetAndChangePosition(mLen / 2, mLen / 2, mLen / 2);
    
    ModeCountCellOperatorArgs args = new ModeCountCellOperatorArgs(
        new SilvermanBandwidthSelector(), 
        new SilvermanBandwidthSelector(), 
        20,
        DoG,
        0,
        new double[] {1,1,1});
    
    KernelDensityEstimator kdeSpy = Mockito.spy(new KernelDensityEstimator(new GaussianKernel()));
    
    ModeCountCellOperator op = new ModeCountCellOperator(
        kdeSpy, 
        new MathsService(log), 
        new GradientService());
    
    int numModes = op.execute(gridIt, args).getNumModes();
    
    assertThat(numModes, is(1));
    // below: code for excluding use of gradient if close to zero (currently disabled)
//    verify(kdeSpy, atLeast(1)).unscaledDensityWithArray(anyDouble(), any(), anyDouble());
//    verify(kdeSpy, never()).unscaledDensityProductAt(anyDouble(), anyDouble(), any(), any(), anyDouble(), anyDouble());
  }
  

}
