package grid.axis;

import static java.lang.System.lineSeparator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import io.log.Log;

import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class AxisService {

  private final Log log;

  @Inject
  public AxisService(Log log) {
    this.log = log;
  }

  public Axis createRegularAxis(SortedSet<Double> ticks,
      Dimension dimension) {

    LinkedList<Double> differences = new LinkedList<Double>();
    int tickNum = 0;
    
    // check for a regular axis
    double lastTick = Double.NaN;
    for (double tick : ticks) {
      tickNum++;
      // set up first entry
      if (tickNum == 1) {
        lastTick = tick;
        continue;
      }
      
      // delta rounded to 1 dp
      double delta = Math.round( (tick - lastTick) * 10.0) / 10.0;
      differences.add(delta);
      lastTick = tick;
    }
    
    int numTicks = differences.stream()
        .filter(d -> d != 0.0).mapToInt(e -> 1).sum() + 1;
    
    // tally up the differences that aren't zero
    Map<Double, Long> differencesTally = differences.stream()
        .filter(d -> d != 0.0)
        .collect(groupingBy(identity(), counting()));
    
    // check for distinct differences
    if (differencesTally.keySet().stream().count() > 1) {
      String differenceSummary = differencesTally.entrySet().stream()
          .map(es -> es.getValue() + ": " + es.getKey())
          .collect(joining(lineSeparator()));
      
      log.printErrorMessage(dimension + " axis is not regular. Tick differences summarised below.");
      log.printErrorMessage(differenceSummary);
      
      return null;
    }
    
    // otherwise use avg as delta
    
    double min = ticks.first();
    double delta = differencesTally.keySet().stream()
        .collect(Collectors.averagingDouble(d -> d));
    return new Axis(dimension, min, delta, numTicks);
  }

}
