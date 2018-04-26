package grid.operator;

import grid.mask.MaskedGridIterator;
import kde.bw.BandwidthSelector;

public class ProjectionBandwidthCellOperatorArgs {

  private final BandwidthSelector bandwidthSelector;
  private final MaskedGridIterator<Double[]> DoG;
  private final double[] normalisedScales;

  public ProjectionBandwidthCellOperatorArgs(
      BandwidthSelector bandwidthSelector, MaskedGridIterator<Double[]> DoG,
      double[] normalisedScales) {
    this.bandwidthSelector = bandwidthSelector;
    this.DoG = DoG;
    this.normalisedScales = normalisedScales;
  }

  public BandwidthSelector getBandwidthSelector() {
    return bandwidthSelector;
  }

  public MaskedGridIterator<Double[]> getDoG() {
    return DoG;
  }

  public double[] getNormalisedScales() {
    return normalisedScales;
  }

}
