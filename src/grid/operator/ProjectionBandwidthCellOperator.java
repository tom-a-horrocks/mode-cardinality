package grid.operator;

import java.util.ArrayList;

import kde.bw.BandwidthSelector;

import com.google.inject.Inject;

import grid.mask.MaskedGridIterator;

public class ProjectionBandwidthCellOperator extends
    CellOperator<Double, Double, ProjectionBandwidthCellOperatorArgs> {

  private final GradientService gradientService;
  private final ArrayList<Double> projections;

  @Inject
  public ProjectionBandwidthCellOperator(GradientService gradientService) {
    super(Double.class);
    this.gradientService = gradientService;
    projections = new ArrayList<Double>();
  }

  @Override
  public Double execute(MaskedGridIterator<Double> it,
      ProjectionBandwidthCellOperatorArgs args) {
    projections.clear();

    BandwidthSelector bandwidthSelector = args.getBandwidthSelector();
    MaskedGridIterator<Double[]> DoG = args.getDoG();
    double[] scales = args.getNormalisedScales();

    // calculate gradient (DoG is reset)
    double[] gradient = gradientService.calculateGradient(it, DoG);
    gradientService.normaliseInPlace(gradient);

    // project and save results
    while (it.hasNext()) {
      double p = it.getProjectedNextMaskCoordinate(gradient, scales);
      projections.add(p);
      it.next();
    }

    return bandwidthSelector.bandwidth(projections);
  }

}
