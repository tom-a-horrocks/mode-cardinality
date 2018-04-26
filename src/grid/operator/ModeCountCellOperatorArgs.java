package grid.operator;

import grid.mask.MaskedGridIterator;
import kde.bw.BandwidthSelector;

public class ModeCountCellOperatorArgs {

  private final int numSubDivisions;
  private final MaskedGridIterator<Double[]> DoG;
  private final BandwidthSelector projectionBandwidthSelector;
  private final BandwidthSelector attributeBandwidthSelector;
  private final double gradientZeroThreshold;
  private final double[] normalisedScales;

  public ModeCountCellOperatorArgs(BandwidthSelector bandwidthSelector,
      BandwidthSelector projectionBandwidthSelector, int numSubDivisions,
      MaskedGridIterator<Double[]> DoG, double gradientZeroThreshold,
      double[] normalisedScales) {
    this.attributeBandwidthSelector = bandwidthSelector;
    this.projectionBandwidthSelector = projectionBandwidthSelector;
    this.numSubDivisions = numSubDivisions;
    this.DoG = DoG;
    this.gradientZeroThreshold = gradientZeroThreshold;
    this.normalisedScales = normalisedScales;
  }

  public BandwidthSelector getAttributeBandwidthSelector() {
    return attributeBandwidthSelector;
  }

  public BandwidthSelector getProjectionBandwidthSelector() {
    return projectionBandwidthSelector;
  }

  public int getNumSubDivisions() {
    return numSubDivisions;
  }

  public MaskedGridIterator<Double[]> getDoG() {
    return DoG;
  }

  public double getGradientZeroThreshold() {
    return gradientZeroThreshold;
  }

  public double[] getNormalisedScales() {
    return normalisedScales;
  }

}
