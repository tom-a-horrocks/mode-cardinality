package grid.operator;

import java.util.ArrayList;

import utility.MathsService;
import kde.KernelDensityEstimator;
import kde.bw.BandwidthSelector;

import com.google.inject.Inject;

import grid.mask.MaskedGridIterator;

public class ModeCountCellOperator extends
    CellOperator<Double, ModeCountCellResult, ModeCountCellOperatorArgs> {

  private static final int MISSING_DATA_CODE = -1;

  private final KernelDensityEstimator kde;
  private final MathsService mathsService;
  private final GradientService gradientService;

  private final ArrayList<Double> attributes;
  private final ArrayList<Double> projections;
  
  private int nCellsNoGradientUsed = 0;
  private int nCellsGradientUsed = 0;

  @Inject
  public ModeCountCellOperator(KernelDensityEstimator kde,
      MathsService mathsService, GradientService gradientService) {
    super(ModeCountCellResult.class);
    this.kde = kde;
    this.mathsService = mathsService;
    this.gradientService = gradientService;

    attributes = new ArrayList<Double>();
    projections = new ArrayList<Double>();
  }

  @Override
  public ModeCountCellResult execute(MaskedGridIterator<Double> it,
      ModeCountCellOperatorArgs args) {
      
    if (!it.centreValueIsValid()) {
      return new ModeCountCellResult(MISSING_DATA_CODE);
    }
      
    // clear caches
    attributes.clear();
    projections.clear();

    // Get arguments
    int numSubDivisions = args.getNumSubDivisions();
    BandwidthSelector attributeBandwidthSelector = args.getAttributeBandwidthSelector();
    BandwidthSelector projectionBandwidthSelector = args.getProjectionBandwidthSelector();
    MaskedGridIterator<Double[]> DoG = args.getDoG();
    double gradientZeroThreshold = args.getGradientZeroThreshold();
    double[] scales = args.getNormalisedScales();
        
    /* ATTRIBUTE CALCULATIONS */
    // caching
    while (it.hasNext()) {
      double a = it.next();
      attributes.add(a);
    }
    it.reset();   
    // ranges; -1 from subdivisons because we check AT the min value
    double attbMin = mathsService.min(attributes);
    double attbMax = mathsService.max(attributes);
    double attbDelta = (attbMax - attbMin) / (numSubDivisions - 1);
    double attbBw = attributeBandwidthSelector.bandwidth(attributes);
    
    if (attbBw == 0) {
      return new ModeCountCellResult(MISSING_DATA_CODE);
    }
    
    /* Gradient calculations */
    double[] gradient = gradientService.calculateGradient(it, DoG);
    double gradMag = gradientService.magnitude(gradient);
    // normalise gradient to avoid projection errors.
    gradientService.normaliseInPlace(gradient, gradMag);

    /* 1D KDE */
    if (gradMag < gradientZeroThreshold) {
      double[] pdf = new double[numSubDivisions];
      for (int i = 0; i < numSubDivisions; i++) {
        double a = attbMin + i * attbDelta;
        double f = kde.unscaledDensityWithArray(a, attributes, attbBw);
        pdf[i] = f;
      }
      nCellsNoGradientUsed++;
      
      int nModes = mathsService.countPeaks(pdf, numSubDivisions);
      return new ModeCountCellResult(nModes, gradMag, gradient, false);
    }
    
    /* PROJECTION CALCULATIONS */
    // caching
    while (it.hasNext()) {
      double p = it.getProjectedNextMaskCoordinate(gradient, scales);
      projections.add(p);
      it.next();
    }

    // ranges; -1 from subdivisons because we check AT the min value
    double projMin = mathsService.min(projections);
    double projMax = mathsService.max(projections);
    double projDelta = (projMax - projMin) / (numSubDivisions - 1);
    double projBw = projectionBandwidthSelector.bandwidth(projections);
    
    if (projBw == 0) {
      return new ModeCountCellResult(MISSING_DATA_CODE);
    }
    
    /* 2D KDE */
    double[] aEvals = new double[numSubDivisions];
    double[] pEvals = new double[numSubDivisions];
    for (int i = 0; i < numSubDivisions; i++) {
      aEvals[i] = attbMin + i * attbDelta;
      pEvals[i] = projMin + i * projDelta;
    }
    
    double[][] pdf = new double[numSubDivisions][numSubDivisions]; // indexed by attb then proj
    kde.unscaledDensityProductBatch(aEvals, pEvals, attributes, projections, attbBw, projBw, pdf);
    
    nCellsGradientUsed++;
    int nModes = mathsService.countPeaks(pdf, numSubDivisions, numSubDivisions);
    return new ModeCountCellResult(nModes, gradMag, gradient, true);
  }
  
  public double getPercentageOfCellsThatUsedGradient() {
    return 100d * nCellsGradientUsed / (nCellsGradientUsed + nCellsNoGradientUsed);
  }

}
