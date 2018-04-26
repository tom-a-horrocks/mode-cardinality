package kde.kernel;

public class GaussianKernel implements Kernel {

  private static final double CONSTANT = 1d / Math.sqrt(2 * Math.PI);

  @Override
  public double at(double loc) {
    return Math.exp(-0.5d * loc * loc);
  }

  @Override
  public double getConstant() {
    return CONSTANT;
  }

}
