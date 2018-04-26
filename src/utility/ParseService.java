package utility;

public class ParseService {

  public boolean parseBoolean(String boolString) {
    assert boolString != null;
    
    if (boolString.equalsIgnoreCase("true")) {
      return true;
    } 
    
    if (boolString.equalsIgnoreCase("false")) {
      return false;
    }
    
    throw new IllegalArgumentException(
        "log-transform must be 'true' or 'false'.");
  }

}
