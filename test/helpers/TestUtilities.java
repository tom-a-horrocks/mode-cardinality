package helpers;

import grid.axis.Dimension;
import io.parse.CommandArguments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TestUtilities {
  
  public static CommandArguments commandArgumentsWithNamedParameters(String... namedParameters) {
    HashMap<String, String> params = new HashMap<String, String>();
    for (int i = 0; i < namedParameters.length; i += 2) {
      params.put(namedParameters[i], namedParameters[i + 1]);
    }
    return new CommandArguments(params);
  }

  public static int attbVal(int i, int j, int k) {
    return 9 * i + 3 * j + k;
  }

  public static double attbVal(double x, double y, double z) {
    // make unique number by removing decimal point and catting
    String xStr = Double.toString(x).replace(".", "");
    String yStr = Double.toString(y).replace(".", "");
    String zStr = Double.toString(z).replace(".", "");

    return Integer.parseInt(xStr + yStr + zStr);
  }

  @SafeVarargs
  public static <T> List<T> list(T... values) {
    return Arrays.asList(values);
  }

  @SafeVarargs
  public static <T> T[] arr(T... elements) {
    return elements;
  }

  @SafeVarargs
  public static <T> Set<T> set(T... elements) {
    return new HashSet<T>(Arrays.asList(elements));
  }

  public static final Dimension X = Dimension.X;
  public static final Dimension Y = Dimension.Y;
  public static final Dimension Z = Dimension.Z;

  public static Double[][][] gridWithIncrementalValuesFromZero(int iLength,
      int jLength, int kLength) {
    Double[][][] grid = new Double[iLength][jLength][kLength];
    for (int i = 0; i < iLength; i++) {
      for (int j = 0; j < jLength; j++) {
        for (int k = 0; k < kLength; k++) {
          grid[i][j][k] = (double) (jLength * kLength * i + kLength * j + k);
        }
      }
    }
    return grid;
  }

  public static Double[][][] getInterfaceVoxet() {
    return new Double[][][] {{{0.520416,0.491637,0.536808,0.331262,0.289326},{0.442013,0.391137,0.323801,0.217304,0.317204},{0.470489,0.38438,0.393335,0.372555,0.216837},{0.30476,0.354174,0.304967,0.384251,0.249022},{0.423779,0.392521,0.333374,0.461815,0.291606}},{{0.504435,0.418746,0.222248,0.306702,0.290708},{0.521642,0.451446,0.320762,0.23985,0.247919},{0.437459,0.432406,0.469929,0.27459,0.22084},{0.499684,0.474391,0.316396,0.327224,0.364934},{0.5858,0.405877,0.367059,0.402754,0.263021}},{{0.496626,0.408208,0.474564,0.371462,0.293488},{0.418959,0.481858,0.347496,0.337606,0.279134},{0.490595,0.461762,0.335089,0.397788,0.211505},{0.380099,0.463585,0.407475,0.358398,0.214345},{0.38728,0.35065,0.443863,0.388762,0.256384}},{{0.429856,0.493995,0.401573,0.264478,0.37681},{0.363084,0.444508,0.274107,0.317198,0.298419},{0.444044,0.361345,0.331141,0.355696,0.223944},{0.382985,0.394976,0.424034,0.318246,0.209137},{0.335921,0.427698,0.394621,0.32938,0.281357}},{{0.50685,0.423339,0.382273,0.241248,0.402979},{0.501074,0.463524,0.348213,0.249155,0.291536},{0.411695,0.440602,0.342428,0.195628,0.299751},{0.464844,0.420271,0.49988,0.329666,0.274411},{0.463621,0.400908,0.437246,0.464378,0.373671}}};
  }
  
  public static Double[][][] getHomogeneousVoxet() {
    return new Double[][][] {{{0.389472,0.438882,0.481037,0.497311,0.320428},{0.310336,0.517176,0.485932,0.550013,0.369779},{0.387047,0.468695,0.347821,0.419282,0.366503},{0.452062,0.440533,0.475643,0.484991,0.236807},{0.500671,0.450131,0.483906,0.450284,0.522278}},{{0.411752,0.452247,0.532515,0.425034,0.548153},{0.491499,0.384864,0.55737,0.575265,0.487632},{0.485779,0.411282,0.414481,0.306389,0.500783},{0.429316,0.409596,0.481843,0.492935,0.464435},{0.499306,0.401078,0.310713,0.332083,0.398396}},{{0.345505,0.424897,0.38384,0.3847,0.435175},{0.370335,0.368671,0.474374,0.500513,0.346055},{0.430975,0.299308,0.476267,0.376446,0.425684},{0.55006,0.345014,0.401674,0.415304,0.300117},{0.452206,0.559945,0.458566,0.499115,0.370507}},{{0.453969,0.449788,0.444821,0.414661,0.444404},{0.407251,0.485578,0.412897,0.478961,0.482513},{0.451388,0.40012,0.383084,0.52473,0.451525},{0.360748,0.302056,0.498838,0.54904,0.373367},{0.413576,0.389665,0.427104,0.317134,0.457655}},{{0.442374,0.315084,0.52836,0.475022,0.347887},{0.336045,0.452706,0.397067,0.466694,0.408675},{0.508943,0.49669,0.406091,0.435118,0.383381},{0.411598,0.573521,0.36771,0.432304,0.363668},{0.516987,0.384992,0.357931,0.383909,0.387595}}};
  }
}
