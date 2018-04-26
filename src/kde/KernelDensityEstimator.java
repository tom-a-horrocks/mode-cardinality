package kde;

import java.util.ArrayList;
import java.util.Iterator;

import kde.kernel.Kernel;

import com.google.inject.Inject;

public class KernelDensityEstimator {

  private Kernel kernel;

  @Inject
  public KernelDensityEstimator(Kernel kernel) {
    this.kernel = kernel;
  }

  public double densityAt(double evaluateAt, Iterator<Double> data,
      double bandwidth) {
    double kernelSum = 0;
    int n = 0;

    while (data.hasNext()) {
      double d = data.next();
      double argScaledByBandwidth = (evaluateAt - d) / bandwidth;
      kernelSum += kernel.at(argScaledByBandwidth);
      n++;
    }
    return kernel.getConstant() * kernelSum / (n * bandwidth);
  }

  public double unscaledDensityWithIterator(double evaluateAt, Iterator<Double> data,
      double bandwidth) {
    double kernelSum = 0;
    int n = 0;
    while (data.hasNext()) {
      double d = data.next();
      double argScaledByBandwidth = (evaluateAt - d) / bandwidth;
      kernelSum += kernel.at(argScaledByBandwidth);
      n++;
    }
    return kernelSum / n;
  }
  
  public double unscaledDensityWithArray(double x, ArrayList<Double> attributes,
      double bandwidth) {
    double kernelSum = 0;
    int n = attributes.size();
    for (int i = 0; i < n; i++) {
      double d = attributes.get(i);
      double argScaledByBandwidth = (x - d) / bandwidth;
      kernelSum += kernel.at(argScaledByBandwidth);
    }
    return kernelSum / n;
  }

  // not currently used (O(n^2) for grid search)
  public double unscaledDensityProductAt(double x1, double x2,
      ArrayList<Double> data1, ArrayList<Double> data2, double bw1, double bw2) {
    double kernelSum = 0;
    int n = data1.size();

    for (int i = 0; i < n; i++) {
      double d1 = data1.get(i);
      double d2 = data2.get(i);
      
      double k1 = kernel.at((x1 - d1) / bw1);
      double k2 = kernel.at((x2 - d2) / bw2);

      kernelSum += k1 * k2;
    }
    return kernelSum / n;
  }

  /**
   * Calculates 2d kde using linear method.
   * 
   * n = |data1| = |data2|.
   * 
   * @param x1Evals x1 values to be evaluated
   * @param x2Evals x2 values to be evaluated
   * @param data1 x1 values KDE is based off
   * @param data2 x2 values KDE is based off
   * @param bw1 bandwidth for x2 dimension
   * @param bw2 bandwidth for x2 dimension
   * @param out calculated 2d pdf, indexes match (x1, x2) in x1Evals, x2Evals
   */
  public void unscaledDensityProductBatch(double[] x1Evals, double[] x2Evals,
      ArrayList<Double> data1, ArrayList<Double> data2,
      double bw1, double bw2, double[][] out) {
    int m1 = x1Evals.length; // number of x1s to evaluate
    int m2 = x2Evals.length; // number of x2s to evaluate
    int n = data1.size(); // number of points in kde dataset
    
    // create array of kernel contributions for every x1 to be evaluated
    double[][] x1KernelContributions = new double [m1][n];
    for (int i = 0; i < m1; i++) {
      double x1 = x1Evals[i];
      for (int j = 0; j < n; j++) {
        double d1 = data1.get(j);
        double k1 = kernel.at((x1 - d1) / bw1);
        x1KernelContributions[i][j] = k1;    
      }
    }
    
    // create array of kernel contributions for every x2 to be evaluated
    double[][] x2KernelContributions = new double [m2][n];
    for (int i = 0; i < m2; i++) {
      double x2 = x2Evals[i];
      for (int j = 0; j < n; j++) {
        double d2 = data2.get(j);
        double k2 = kernel.at((x2 - d2) / bw2);
        x2KernelContributions[i][j] = k2;    
      }
    }
    
    // do dot product for each (x,y) and return in out
    for (int i = 0; i < m1; i++) {
      for (int j = 0; j < m2; j++) {
        out[i][j] = dot(x1KernelContributions[i], x2KernelContributions[j], n);
      }
    }
    
  }

  private double dot(double[] arr1, double[] arr2, int n) {
    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += arr1[i] * arr2[i];
    }
    return sum;
  }

}