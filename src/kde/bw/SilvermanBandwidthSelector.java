package kde.bw;

import java.util.ArrayList;
import java.util.Iterator;

public class SilvermanBandwidthSelector implements BandwidthSelector {

  private static final double CONST = Math.pow(4d / 3, 1d / 5);

  // fix signature
  @Override
  public double bandwidth(Iterator<Double> iterator) {
    boolean allSame = true;
    boolean firstRun = true;
    double prevValue = Double.NaN;
    int n = 0;
    double s1 = 0;
    double s2 = 0;
    while (iterator.hasNext()) {
      double val = iterator.next();
      if (!firstRun) {
        allSame = allSame && prevValue == val;
      } else {
        firstRun = false;
      }
      prevValue = val;

      n++;
      s1 += val;
      s2 += val * val;
    }
    if (allSame) {
      return 0;
    }

    double stdev = Math.sqrt((n * s2 - s1 * s1) / (n * (n - 1)));

    return CONST * stdev * Math.pow(n, -1d / 5);
  }

  @Override
  public String toString() {
    return "SilvermanBandwidthSelector";
  }

  @Override
  public double bandwidth(ArrayList<Double> data) {
    boolean allSame = true;
    boolean firstRun = true;
    double prevValue = Double.NaN;
    int n = data.size();
    double s1 = 0;
    double s2 = 0;

    for (int i = 0; i < n; i++) {
      double val = data.get(i);
      if (!firstRun) {
        allSame = allSame && prevValue == val;
      } else {
        firstRun = false;
      }
      prevValue = val;

      s1 += val;
      s2 += val * val;
    }
    if (allSame) {
      return 0;
    }

    double stdev = Math.sqrt((n * s2 - s1 * s1) / (n * (n - 1)));

    return CONST * stdev * Math.pow(n, -1d / 5);
  }
}
