package kde.bw;

import kde.bw.SilvermanBandwidthSelector;
import grid.mask.MaskedGridIterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class SilvermanBandwidthSelectorTest {
  
  @Mock MaskedGridIterator<Double> iterator;
  
  SilvermanBandwidthSelector silvermanBandwidthSelector;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    silvermanBandwidthSelector = new SilvermanBandwidthSelector();
  }

  @Test
  public void testBandwidthSanity() throws Exception {
    when(iterator.next()).thenReturn(1d, 2d, 3d);
    when(iterator.hasNext()).thenReturn(true, true, true, false);
    
    double bw = silvermanBandwidthSelector.bandwidth(iterator);
    
    assertThat(bw, is(closeTo(0.850283, 1e-5d)));
  }
  
  @Test
  public void testBandwidthAllSame() throws Exception {
    when(iterator.next()).thenReturn(1d/3, 1d/3, 1d/3);
    when(iterator.hasNext()).thenReturn(true, true, true, false);
    
    double bw = silvermanBandwidthSelector.bandwidth(iterator);
    
    assertThat(bw, is(0d));
  }

}
