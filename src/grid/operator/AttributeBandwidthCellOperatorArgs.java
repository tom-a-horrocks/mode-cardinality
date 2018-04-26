package grid.operator;

import kde.bw.BandwidthSelector;

public class AttributeBandwidthCellOperatorArgs {

  private final BandwidthSelector bandwidthSelector;

  public AttributeBandwidthCellOperatorArgs(BandwidthSelector bandwidthSelector) {
    this.bandwidthSelector = bandwidthSelector;
  }

  public BandwidthSelector getBandwidthSelector() {
    return bandwidthSelector;
  }

}
