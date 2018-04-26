package kde.kernel;

import static org.junit.Assert.assertEquals;
import kde.kernel.GaussianKernel;

import org.junit.Before;
import org.junit.Test;

public class GaussianKernelTest {

  private GaussianKernel gaussianKernel;

  @Before
  public void setUp() throws Exception {
    gaussianKernel = new GaussianKernel();
  }

  @Test
  public void evaluatedToOneAtCenter() {
    assertEquals(1, gaussianKernel.at(0), 0);
  }

  @Test
  public void correctValueAtPositiveOne() {
    assertEquals(Math.exp(-0.5d), gaussianKernel.at(1), 0.000001);
  }

  @Test
  public void correctValueAtNegativeOne() {
    assertEquals(Math.exp(-0.5d), gaussianKernel.at(-1), 0.000001);
  }

}
