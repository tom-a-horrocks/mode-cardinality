package kde.kernel;

public class UniformKernel implements Kernel {
  
  @Override
  public double at(double loc) {
    return Math.abs(loc) <= 1 ? 0.5 : 0;
  }

  @Override
  public double getConstant() {
    return 1;
  }

}
