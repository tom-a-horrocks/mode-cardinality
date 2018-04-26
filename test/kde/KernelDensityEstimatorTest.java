package kde;

import static helpers.TestUtilities.list;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import kde.kernel.GaussianKernel;
import kde.kernel.Kernel;
import kde.kernel.UniformKernel;

import org.junit.Before;
import org.junit.Test;

public class KernelDensityEstimatorTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void gaussianKernelDensityIntegratesToOne() {
    Kernel kernel = new GaussianKernel();

    KernelDensityEstimator kde = new KernelDensityEstimator(kernel);
    List<Double> seedDistribution = list(1d, 2d, 3d, 4d);

    double integral = integrateFromNegativeOneHundredToPositiveOneHundred(kde, seedDistribution);
    
    assertEquals(1d, integral, 0.001);
  }
  
  @Test
  public void UniformKernelDensityIntegratesToOne() {
    Kernel kernel = new UniformKernel();

    KernelDensityEstimator kde = new KernelDensityEstimator(kernel);
    List<Double> seedDistribution = list(1d, 2d, 3d, 4d);

    double integral = integrateFromNegativeOneHundredToPositiveOneHundred(kde, seedDistribution);
    
    assertEquals(1d, integral, 0.001);
  }
  
  @Test
  public void gaussianKernelDensityCalculatedCorrectly() {
    Kernel kernel = new GaussianKernel();

    KernelDensityEstimator kde = new KernelDensityEstimator(kernel);
    List<Double> seedDistribution = list(1d, 2d, 3d, 4d);
    
    double density = kde.densityAt(3, seedDistribution.listIterator(), 5);
    
    assertThat(density, is(closeTo(0.0774649, 0.00001d)));
  }
  
  private double integrateFromNegativeOneHundredToPositiveOneHundred(KernelDensityEstimator kde,
      List<Double> seedDistribution) {
    double bandwidth = 5;
    double lastDensity = kde.densityAt(-100, seedDistribution.listIterator(), bandwidth);
    double integral = 0;
    for (double x = -99.9; x < 100; x += 0.1) {
      double nextDensity = kde.densityAt(x, seedDistribution.listIterator(), bandwidth);
      double avg = (nextDensity + lastDensity)/2;
      integral += avg * (0.1);
      lastDensity = nextDensity;
    }
    return integral;
  }

}
