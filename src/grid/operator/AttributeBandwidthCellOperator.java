package grid.operator;

import com.google.inject.Inject;

import grid.mask.MaskedGridIterator;

public class AttributeBandwidthCellOperator extends
    CellOperator<Double, Double, AttributeBandwidthCellOperatorArgs> {

  @Inject
  public AttributeBandwidthCellOperator() {
    super(Double.class);
  }

  @Override
  public Double execute(MaskedGridIterator<Double> maskedGridIterator,
      AttributeBandwidthCellOperatorArgs args) {
    return args.getBandwidthSelector().bandwidth(maskedGridIterator);
  }

}
