package grid.mask;

import static grid.axis.Dimension.X;
import static grid.axis.Dimension.Y;
import static grid.axis.Dimension.Z;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import grid.Grid;
import grid.axis.Axes;
import grid.axis.Axis;
import grid.axis.Dimension;
import grid.mask.MaskService;
import grid.mask.EllipsoidRadius;
import grid.mask.Mask;
import io.log.Log;

import java.util.EnumMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MaskServiceTest {

  private MaskService maskService;
  private Axes axes;
  
  private static final double PDF_CONST = 1d / (Math.pow(2 * Math.PI, 1.5) * 0.5 * 0.5 * 0.5);

  @Mock
  Log log;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    EnumMap<Dimension, Axis> axisMap = new EnumMap<Dimension, Axis>(
        Dimension.class);

    axisMap.put(Dimension.X, new Axis(Dimension.X, 0, 30, 5));
    axisMap.put(Dimension.Y, new Axis(Dimension.Y, 0, 30, 5));
    axisMap.put(Dimension.Z, new Axis(Dimension.Z, 0, 15, 5));

    axes = new Axes(axisMap);

    maskService = new MaskService(log);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void twoNeighbours() {
    Mask accessMask = maskService.createAccessMask(axes, new EllipsoidRadius(15));

    // above and below only.
    boolean[][][] boolArray = { { { true, true, true } } };

    Mask expected = new Mask(boolArray, 0, 0, 1, 1, 1, 3);

    assertThat(accessMask, is(expected));
  }

  @Test
  public void eightNeighbours() {
    Mask accessMask = maskService.createAccessMask(axes, new EllipsoidRadius(30));

    boolean[][][] boolArray = new boolean[3][3][5];
    // two above and below (and centre)
    for (int k = 0; k < 5; k++) {
      boolArray[1][1][k] = true;
    }
    // x-dirn
    boolArray[0][1][2] = true;
    boolArray[2][1][2] = true;
    // y-dirn
    boolArray[1][0][2] = true;
    boolArray[1][2][2] = true;

    Mask expected = new Mask(boolArray, 1, 1, 2, 3, 3, 5);

    assertThat(accessMask, is(expected));
  }

  @Test
  public void derivativeOfGaussianRandomPoint() {
    // answer generated from mathematica

    Double[] DoG = maskService.derivativeOfGaussian(1d, 0d, -2d, 1, 1, 1);

    double xAnswer = -1 * Math.exp(-2.5);
    double yAnswer = 0;
    double zAnswer = 2 * Math.exp(-2.5);

    assertThat(DoG[0], is(closeTo(xAnswer, 0.0001d)));
    assertThat(DoG[1], is(closeTo(yAnswer, 0.0001d)));
    assertThat(DoG[2], is(closeTo(zAnswer, 0.0001d)));
  }
  
  @Test
  public void derivativeOfGaussianCorner() {
    // answer generated from mathematica

    Double[] DoG = maskService.derivativeOfGaussian(-1, -1, -1, 0.5, 0.5, 0.5);

    assertThat(DoG[0], is(closeTo(0.00503632 / PDF_CONST, 0.0001d)));
    assertThat(DoG[1], is(closeTo(0.00503632 / PDF_CONST, 0.0001d)));
    assertThat(DoG[2], is(closeTo(0.00503632 / PDF_CONST, 0.0001d)));
  }
  
  @Test
  public void derivativeOfGaussianGrid() {

    EnumMap<Dimension, Axis> axes = new EnumMap<Dimension, Axis>(
        Dimension.class);
    int min = 0;
    int delta = 1;
    int numTicks = 100;
    axes.put(X, new Axis(X, min, delta, numTicks));
    axes.put(Y, new Axis(Y, min, delta, numTicks));
    axes.put(Z, new Axis(Z, min, delta, numTicks));

    Grid<Double[]> DoGGrid = new MaskService(mock(Log.class))
        .createDerivativeOfGaussianFilterGrid(new Axes(axes), new EllipsoidRadius(delta));

    boolean maskGrid[][][] = new boolean[3][3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 3; k++) {
          maskGrid[i][j][k] = true;
        }
      }
    }
    
    Mask trueMask = new Mask(maskGrid, 1, 1, 1, 3, 3, 3);
    MaskedGridIterator<Double[]> DoG = new MaskedGridIterator<Double[]>(1, 1, 1, DoGGrid, trueMask);
    
    // answers sourced from mathematica
    double[] expectedDoGX = {0.00503632,0.0372136,0.00503632,0.0372136,0.274974,0.0372136,0.00503632,0.0372136,0.00503632,0.,0.,0.,0.,0.,0.,0.,0.,0.,-0.00503632,-0.0372136,-0.00503632,-0.0372136,-0.274974,-0.0372136,-0.00503632,-0.0372136,-0.00503632};
    double[] expectedDoGY = {0.00503632,0.0372136,0.00503632,0.,0.,0.,-0.00503632,-0.0372136,-0.00503632,0.0372136,0.274974,0.0372136,0.,0.,0.,-0.0372136,-0.274974,-0.0372136,0.00503632,0.0372136,0.00503632,0.,0.,0.,-0.00503632,-0.0372136,-0.00503632};
    double[] expectedDoGZ = {0.00503632,0.,-0.00503632,0.0372136,0.,-0.0372136,0.00503632,0.,-0.00503632,0.0372136,0.,-0.0372136,0.274974,0.,-0.274974,0.0372136,0.,-0.0372136,0.00503632,0.,-0.00503632,0.0372136,0.,-0.0372136,0.00503632,0.,-0.00503632};
    
    for (int i = 0; i < expectedDoGX.length; i++) {
      Double[] val = DoG.next();
      // expecting negative DoG because it has been flipped

      assertThat(val[0], is(closeTo(-expectedDoGX[i] / PDF_CONST , 0.00001)));
      assertThat(val[1], is(closeTo(-expectedDoGY[i] / PDF_CONST, 0.00001)));
      assertThat(val[2], is(closeTo(-expectedDoGZ[i] / PDF_CONST, 0.00001)));
    }

  }

}
