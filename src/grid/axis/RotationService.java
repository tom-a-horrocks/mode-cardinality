package grid.axis;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class RotationService {

  public double xOriginalToRegular(double xOriginal, double yOriginal,
      double rotation) {
    return xOriginal * cos(-rotation) - yOriginal * sin(-rotation);
  }

  public double yOriginalToRegular(double xOriginal, double yOriginal,
      double rotation) {
    return xOriginal * sin(-rotation) + yOriginal * cos(-rotation);
  }

  public double xRegularToOriginal(double xRegular, double yRegular,
      double rotation) {
    return xRegular * cos(rotation) - yRegular * sin(rotation);
  }

  public double yRegularToOriginal(double xRegular, double yRegular,
      double rotation) {
    return xRegular * sin(rotation) + yRegular * cos(rotation);
  }

}
