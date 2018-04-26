package utility;

import io.log.Log;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class MathsServiceTest {
  
  @Mock Log log;
  
  @InjectMocks
  MathsService mathsService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void increasingArrayFindsOnePeakAndHasNoLoggedFlatRegions() {
    double[][] notFlat = new double[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        notFlat[i][j] = i+j;
      }
    }
    
    int numPeaks = mathsService.countPeaks(notFlat, 5, 5);
    
    verify(log, never()).printErrorMessage(any());
    assertThat(numPeaks, is(1));
  }
  
  @Test
  public void flatArrayLogsWarningAndFindsNoPeaks() {
    double[][] flat = new double[5][5];
    
    int numPeaks = mathsService.countPeaks(flat, 5, 5);
    
    verify(log).printWarningMessage(Mockito.matches(".*flat.*"));
    assertThat(numPeaks, is(0));
  }
  
  @Test
  public void TwoPeaksAreFoundAndFlatRegionsAreLogged() {
    double[][] pdf = new double[5][5];
    // two peaks
    pdf[2][2] = 10;
    pdf[0][0] = 3;
    
    int nPeaks = mathsService.countPeaks(pdf, 5, 5);
    
    verify(log).printWarningMessage(Mockito.matches(".*flat.*"));
    assertThat(nPeaks, is(2));
  }

}
