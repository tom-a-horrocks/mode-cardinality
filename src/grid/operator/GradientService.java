package grid.operator;

import grid.mask.MaskedGridIterator;

public class GradientService {

  public double[] calculateGradient(MaskedGridIterator<Double> it,
      MaskedGridIterator<Double[]> DoG) {

    double[] gradient = { 0, 0, 0 };

    it.setFillingAndReset(true);
    
    while (it.hasNext()) {
      Double[] gradComponents = DoG.next();
      double gridValue = it.next();
      
      for (int i = 0; i < 3; i++) {
        gradient[i] += gradComponents[i] * gridValue;
      }
    }

    // expected DoG and grid mask to have the exact same entries
    assert !DoG.hasNext();

    // reset iterators
    it.setFillingAndReset(false);
    DoG.reset();

    return gradient;
  }

  public double magnitude(double[] gradient) {
    return Math.sqrt(gradient[0] * gradient[0] + gradient[1] * gradient[1]
        + gradient[2] * gradient[2]);
  }

  public void normaliseInPlace(double[] gradient) {
    double magnitude = magnitude(gradient);
    normaliseInPlace(gradient, magnitude);
  }

  public void normaliseInPlace(double[] gradient, double magnitude) {
    gradient[0] = gradient[0] / magnitude;
    gradient[1] = gradient[1] / magnitude;
    gradient[2] = gradient[2] / magnitude;
  }

}
