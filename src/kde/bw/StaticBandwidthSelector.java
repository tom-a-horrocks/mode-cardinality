package kde.bw;

import java.util.ArrayList;
import java.util.Iterator;

public class StaticBandwidthSelector implements BandwidthSelector {

  private final double bandwidth;

  public StaticBandwidthSelector(double bandwidth) {
    this.bandwidth = bandwidth;
  }

  @Override
  public double bandwidth(
      Iterator<Double> iterator) {
    return bandwidth;
  }

  @Override
  public String toString() {
    return "StaticBandwidthSelector [bandwidth=" + bandwidth + "]";
  }

  @Override
  public double bandwidth(ArrayList<Double> data) {
    return bandwidth;
  }

}
