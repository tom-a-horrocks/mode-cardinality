package helpers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class DeepEqualsDouble2D implements Matcher<double[][]> {

  private double[][] correctArray;
  String descriptionText;

  public DeepEqualsDouble2D(double[][] correctArray) {
    this.correctArray = correctArray;
    descriptionText = "";
  }

  @Override
  public void describeTo(Description description) {
  }

  @Override
  public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
  }

  @Override
  public void describeMismatch(Object item, Description mismatchDescription) {
    mismatchDescription.appendText(descriptionText);
  }

  @Override
  public boolean matches(Object item) {
    try {
      double[][] against = (double[][]) item;
      if (correctArray.length != against.length) {
        descriptionText = "array.length was " + against.length + ", expected " + correctArray.length;
        return false;
      }
      for (int i = 0; i < correctArray.length; i++) {
        if (correctArray[i].length != against[i].length) {
          descriptionText = "array["+i+"].length was " + against[i].length + ", expected " + correctArray[i].length;
          return false;
        }
        for (int j = 0; j < correctArray[i].length; j++) {
          if (correctArray[i][j] != against[i][j]) {
            descriptionText = "array["+i+"]["+j+"] was " + against[i][j] + ", expected " + correctArray[i][j];
            return false;
          }
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

}
