package kde.bw;

import java.util.ArrayList;
import java.util.Iterator;

public class MultipliedBandwidthSelector implements BandwidthSelector {

  private final BandwidthSelector bandwidthSelector;
  private final double bandwidthMultiplier;

  public MultipliedBandwidthSelector(BandwidthSelector bandwidthSelector,
      double bandwidthMultiplier) {
    this.bandwidthSelector = bandwidthSelector;
    this.bandwidthMultiplier = bandwidthMultiplier;
  }

  @Override
  public double bandwidth(Iterator<Double> iterator) {
    return bandwidthMultiplier * bandwidthSelector.bandwidth(iterator);
  }

  @Override
  public double bandwidth(ArrayList<Double> data) {
    return bandwidthMultiplier * bandwidthSelector.bandwidth(data);
  }

}
