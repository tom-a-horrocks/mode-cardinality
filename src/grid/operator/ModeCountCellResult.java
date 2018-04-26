package grid.operator;

public class ModeCountCellResult {

  private static final String DELIM = " ";
  private final int nModes;
  private final double gradientMag;
  private final double xGradient;
  private final double yGradient;
  private final double zGradient;
  private final boolean isGradientUsed;

  public ModeCountCellResult(int nModes, double gradientMag,
      double[] normalisedGradient, boolean isGradientUsed) {
    this.nModes = nModes;
    this.gradientMag = gradientMag;
    this.xGradient = normalisedGradient[0];
    this.yGradient = normalisedGradient[1];
    this.zGradient = normalisedGradient[2];
    this.isGradientUsed = isGradientUsed;
  }
  
  public ModeCountCellResult(int missingDataCode) {
    this.nModes = missingDataCode;
    this.gradientMag = missingDataCode;
    this.xGradient = missingDataCode;
    this.yGradient = missingDataCode;
    this.zGradient = missingDataCode;
    this.isGradientUsed = false;
  }

  @Override
  public String toString() {
    // used when exporting file
    return nModes + DELIM + 
        gradientMag + DELIM + 
        xGradient + DELIM + 
        yGradient + DELIM +
        zGradient + DELIM + 
        (isGradientUsed ? 1 : 0);
  }

  public int getNumModes() {
    return nModes;
  }

}
