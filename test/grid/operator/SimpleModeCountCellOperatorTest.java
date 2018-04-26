package grid.operator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import io.log.Log;
import grid.Grid;
import grid.mask.Mask;
import grid.mask.MaskedGridIterator;
import kde.KernelDensityEstimator;
import kde.bw.BandwidthSelector;
import kde.kernel.GaussianKernel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import utility.MathsService;
import config.AttributeBandwidthSelectorProvider;

@RunWith(MockitoJUnitRunner.class)
public class SimpleModeCountCellOperatorTest {
  
  private SimpleModeCountCellOperator simpleModeCountCellOperator;
  
  @Mock
  private BandwidthSelector bwSelector;
  
  @Mock
  private AttributeBandwidthSelectorProvider bwProvider;

  @Mock
  private Log log;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    simpleModeCountCellOperator = new SimpleModeCountCellOperator(
        new KernelDensityEstimator(new GaussianKernel()), new MathsService(log));
    
    when(bwProvider.get()).thenReturn(bwSelector);
  }

  @Test
  public void findsOneBroadPeakWithLargeBandwidth() {
    Double[][][] grid = {{{1.1, 1.2, 2d, 3d, 4d, 4.1, 4.2}}};
    boolean[][][] mask = {{{true, true, true, true, true, true, true}}};
   
    MaskedGridIterator<Double> mgIterator = new MaskedGridIterator<Double>(
        new Grid<Double>(grid, null, 1, 1, 7), 
        new Mask(mask, 0, 0, 3, 1, 1, 7));
    mgIterator.resetAndChangePosition(0, 0, 3);
       
    when(bwSelector.bandwidth(any())).thenReturn(5d);
    
    int nModes = simpleModeCountCellOperator.execute(mgIterator, new SimpleModeCountCellOperatorArgs(bwSelector, 100));
    
    assertThat(nModes, is(1));
  }
  
  @Test
  public void twoPeaksFoundWithModerateBandwidth() {
    Double[][][] grid = {{{1.1, 1.2, 2d, 3d, 4d, 4.1, 4.2}}};
    boolean[][][] mask = {{{true, true, true, true, true, true, true}}};
   
    MaskedGridIterator<Double> mgIterator = new MaskedGridIterator<Double>(
        new Grid<Double>(grid, null, 1, 1, 7), 
        new Mask(mask, 0, 0, 3, 1, 1, 7));
    mgIterator.resetAndChangePosition(0, 0, 3);
        
    when(bwSelector.bandwidth(any())).thenReturn(0.9);
    
    int nModes = simpleModeCountCellOperator.execute(mgIterator, new SimpleModeCountCellOperatorArgs(bwSelector, 100));
    
    assertThat(nModes, is(2));
  }

  
  @Test
  public void fourPeaksFoundWithSmallBandwidth() {
    Double[][][] grid = {{{1.1, 1.2, 2d, 3d, 4d, 4.1, 4.2}}};
    boolean[][][] mask = {{{true, true, true, true, true, true, true}}};
   
    MaskedGridIterator<Double> mgIterator = new MaskedGridIterator<Double>(
        new Grid<Double>(grid, null, 1, 1, 7), 
        new Mask(mask, 0, 0, 3, 1, 1, 7));
    mgIterator.resetAndChangePosition(0, 0, 3);
    
    when(bwSelector.bandwidth(any())).thenReturn(0.1);
    
    int nModes = simpleModeCountCellOperator.execute(mgIterator, new SimpleModeCountCellOperatorArgs(bwSelector, 100));
    
    assertThat(nModes, is(4));
  }
  
  @Test
  public void findsFiveIndividualPeaks() {
    Double[][][] grid = {{{1d,2d,3d,4d,5d}}};
    boolean[][][] mask = {{{true, true, true, true, true}}};
   
    MaskedGridIterator<Double> mgIterator = new MaskedGridIterator<Double>(
        new Grid<Double>(grid, null, 1, 1, 5), 
        new Mask(mask, 0, 0, 2, 1, 1, 5));
    mgIterator.resetAndChangePosition(0, 0, 2);
    
    when(bwSelector.bandwidth(any())).thenReturn(0.1);
    
    int nModes = simpleModeCountCellOperator.execute(mgIterator, new SimpleModeCountCellOperatorArgs(bwSelector, 100));
    
    assertThat(nModes, is(5));
  }

}
