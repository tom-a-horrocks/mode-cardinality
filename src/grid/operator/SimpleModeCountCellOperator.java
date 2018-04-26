package grid.operator;

import utility.MathsService;
import kde.KernelDensityEstimator;
import kde.bw.BandwidthSelector;

import com.google.inject.Inject;

import grid.mask.MaskedGridIterator;

public class SimpleModeCountCellOperator extends CellOperator<Double, Integer, SimpleModeCountCellOperatorArgs> {
  
  private static final int MISSING_DATA_CODE = -1;
  private final KernelDensityEstimator kde;
  private final MathsService mathsService;

  @Inject
  public SimpleModeCountCellOperator(KernelDensityEstimator kde, MathsService mathsService) {
    super(Integer.class);
    this.kde = kde;
    this.mathsService = mathsService;
  }

  @Override
  public Integer execute(MaskedGridIterator<Double> it, SimpleModeCountCellOperatorArgs args) {
    if (!it.centreValueIsValid()) {
      return MISSING_DATA_CODE;
    }
    
    BandwidthSelector bwSelector = args.getBandwidthSelector();
    
    double bw = bwSelector.bandwidth(it); it.reset();
    int numSubdivisions = args.getNumSubDivisions();

    if (bw == 0) {
      return MISSING_DATA_CODE; // missing data here
    }
    
    double min = mathsService.min(it); it.reset();
    double max = mathsService.max(it); it.reset();
    
    double delta = (max - min) / (numSubdivisions - 1); //subtract 1 because we check at min
    
    // mode search parameters
    int numModes = 0;
    boolean wasIncreasingOrStayedFlat = true; // was increasing on the left by definition
    double lastF = Double.MIN_VALUE; // just matters that this is lower than the first value
    
    // start mode search (1D)
    for (int i = 0; i < numSubdivisions; i++) {
      double x = min + i * delta; // next x pos
      double f = kde.unscaledDensityWithIterator(x, it, bw); // next value of pdf
      boolean isDecreasing = f < lastF;
      if (wasIncreasingOrStayedFlat && isDecreasing) {
        numModes++;
      }
      
      // update for next loop
      wasIncreasingOrStayedFlat = !isDecreasing;
      lastF = f;
      it.reset();
    }
    
    // know that f decreases to the right of max value due to structure of kde.
    if (wasIncreasingOrStayedFlat) {
      numModes++;
    }
    
    return numModes;
  }

}
