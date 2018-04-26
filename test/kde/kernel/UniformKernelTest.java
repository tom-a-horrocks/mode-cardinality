package kde.kernel;

import static org.junit.Assert.assertEquals;
import kde.kernel.Kernel;
import kde.kernel.UniformKernel;

import org.junit.Before;
import org.junit.Test;

public class UniformKernelTest {

  private Kernel unformKernel;

  @Before
  public void setUp() throws Exception {
    unformKernel = new UniformKernel();
  }

  @Test
  public void evaluatedToOneHalfAtCenter() {
    assertEquals(0.5, unformKernel.at(0), 0);
  }

  @Test
  public void evaluatesToOneHalfAtBandwidthBoundary() {
    assertEquals(0.5, unformKernel.at(-1), 0);
    assertEquals(0.5, unformKernel.at(1), 0);
  }

  @Test
  public void evaluatedToZeroOutsideBandwidthBoundary() {
    assertEquals(0, unformKernel.at(1.01), 0);
    assertEquals(0, unformKernel.at(-1.01), 0);
  }

}
