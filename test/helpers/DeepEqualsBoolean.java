package helpers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class DeepEqualsBoolean implements Matcher<boolean[][][]> {

  private boolean[][][] correctArray;

  public DeepEqualsBoolean(boolean[][][] correctArray) {
    this.correctArray = correctArray;
  }

  @Override
  public void describeTo(Description description) {
  }

  @Override
  public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
  }

  @Override
  public void describeMismatch(Object item, Description mismatchDescription) {
  }

  @Override
  public boolean matches(Object item) {
    try {
      boolean[][][] against = (boolean[][][]) item;
      if (correctArray.length != against.length) {
        return false;
      }

      for (int i = 0; i < correctArray.length; i++) {
        if (correctArray[i].length != against[i].length) {
          return false;
        }
        for (int j = 0; j < correctArray[i].length; j++) {
          if (correctArray[i][j].length != against[i][j].length) {
            return false;
          }
          for (int k = 0; k < correctArray[i][j].length; k++) {
            if (correctArray[i][j][k] != against[i][j][k]) {
              return false;
            }
          }
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

}
