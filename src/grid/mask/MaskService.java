package grid.mask;

import static grid.axis.Dimension.X;
import static grid.axis.Dimension.Y;
import static grid.axis.Dimension.Z;

import io.log.Log;

import com.google.inject.Inject;

import grid.Grid;
import grid.axis.Axes;

public class MaskService {

  private final Log log;

  @Inject
  public MaskService(Log log) {
    this.log = log;
  }

  public Mask createAccessMask(Axes axes, EllipsoidRadius ellipsoidRadius) {
    int iMid = axes.numTicksCoveredByLength(ellipsoidRadius.x(), X) - 1;
    int jMid = axes.numTicksCoveredByLength(ellipsoidRadius.y(), Y) - 1;
    int kMid = axes.numTicksCoveredByLength(ellipsoidRadius.z(), Z) - 1;

    int iLength = 2 * iMid + 1;
    int jLength = 2 * jMid + 1;
    int kLength = 2 * kMid + 1;

    boolean[][][] mask = new boolean[iLength][jLength][kLength];
    for (int i = 0; i < iLength; i++) {
      double xDistanceSq = axes.squaredDistance(i, iMid, X);

      for (int j = 0; j < jLength; j++) {
        double yDistanceSq = axes.squaredDistance(j, jMid, Y);

        for (int k = 0; k < kLength; k++) {
          double zDistanceSq = axes.squaredDistance(k, kMid, Z);

          // Check if within ellipsoid
          mask[i][j][k] = (xDistanceSq / ellipsoidRadius.xSquared())
              + (yDistanceSq / ellipsoidRadius.ySquared())
              + (zDistanceSq / ellipsoidRadius.zSquared()) <= 1;
        }
      }
    }

    Mask maskRet = new Mask(mask, iMid, jMid, kMid, iLength, jLength, kLength);
    log.printInformationMessage("Created mask: " + maskRet);
    return maskRet;
  }

  /**
   * Creates a DoG grid with dimensions lengths according to radius. Sets sigma
   * in each dimension to the appropriate r/2.
   * Note that the grid is already -ve, so to convolve just need to multiply.
   * 
   * @param axes
   * @param ellipsoidRadius
   * @return
   */
  public Grid<Double[]> createDerivativeOfGaussianFilterGrid(Axes axes,
      EllipsoidRadius ellipsoidRadius) {
    int iMid = axes.numTicksCoveredByLength(ellipsoidRadius.x(), X) - 1;
    int jMid = axes.numTicksCoveredByLength(ellipsoidRadius.y(), Y) - 1;
    int kMid = axes.numTicksCoveredByLength(ellipsoidRadius.z(), Z) - 1;

    int iLength = 2 * iMid + 1;
    int jLength = 2 * jMid + 1;
    int kLength = 2 * kMid + 1;

    // 3 = no. of dimensions
    Double[][][][] grid = new Double[iLength][jLength][kLength][3];

    double xSigma = ellipsoidRadius.x() / 2;
    double ySigma = ellipsoidRadius.y() / 2;
    double zSigma = ellipsoidRadius.z() / 2;

    for (int i = 0; i < iLength; i++) {
      double x = axes.distance(iMid, i, X);

      for (int j = 0; j < jLength; j++) {
        double y = axes.distance(jMid, j, Y);

        for (int k = 0; k < kLength; k++) {
          double z = axes.distance(kMid, k, Z);

          Double[] DoG = derivativeOfGaussian(x, y, z, xSigma, ySigma, zSigma);
          
          // make negative to prepare for convolution later
          for (int l = 0; l < 3; l++) {
            DoG[l] = - DoG[l];
          }
          grid[i][j][k] = DoG;
        }
      }
    }

    Grid<Double[]> retVal = new Grid<Double[]>(grid, null, iLength, jLength, kLength);
    log.printInformationMessage("DoG created with (xSigma, ySigma, zSigma) = (" + xSigma + "," + ySigma + "," + zSigma + ")");
    return retVal;
  }

  Double[] derivativeOfGaussian(double x, double y, double z, double xSigma,
      double ySigma, double zSigma) {
    
//    double denominatorConstant = -2 * Math.sqrt(2) * Math.pow(Math.PI, 3d / 2) * xSigma * ySigma * zSigma;
    double denominatorConstant = -1; // get rid of to avoid numerical instability
    
    double xSigmaSq = xSigma * xSigma;
    double ySigmaSq = ySigma * ySigma;
    double zSigmaSq = zSigma * zSigma;
    
    double numeratorRadial = Math.exp(-0.5 * (
          (x * x) / xSigmaSq 
        + (y * y) / ySigmaSq 
        + (z * z) / zSigmaSq
        ));
    double coefficient = numeratorRadial / denominatorConstant;

    return new Double[] { (x / xSigmaSq) * coefficient, (y / ySigmaSq) * coefficient, (z / zSigmaSq) * coefficient };
  }
}
