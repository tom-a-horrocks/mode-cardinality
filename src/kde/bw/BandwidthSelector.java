package kde.bw;

import java.util.ArrayList;
import java.util.Iterator;

public interface BandwidthSelector {

  double bandwidth(
      Iterator<Double> iterator);
  
  double bandwidth(
      ArrayList<Double> data);
}
