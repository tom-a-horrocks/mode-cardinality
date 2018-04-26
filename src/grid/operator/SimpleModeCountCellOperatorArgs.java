package grid.operator;

import kde.bw.BandwidthSelector;

public class SimpleModeCountCellOperatorArgs {

  private final BandwidthSelector bandwidthSelector;
  private final int numSubDivisions;

  public SimpleModeCountCellOperatorArgs(BandwidthSelector bandwidthSelector,
      int numSubDivisions) {
    this.bandwidthSelector = bandwidthSelector;
    this.numSubDivisions = numSubDivisions;
  }

  public BandwidthSelector getBandwidthSelector() {
    return bandwidthSelector;
  }

  public int getNumSubDivisions() {
    return numSubDivisions;
  }

}
