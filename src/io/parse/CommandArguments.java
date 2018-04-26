package io.parse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandArguments {

  private Map<String, String> namedParameters = new HashMap<String, String>();

  private List<String> unnamedParameters = new LinkedList<String>();

  public CommandArguments() {
  }

  public CommandArguments(LinkedList<String> unnamedParameters,
      HashMap<String, String> namedParameters) {
    this.unnamedParameters = unnamedParameters;
    this.namedParameters = namedParameters;
  }

  public CommandArguments(Map<String, String> namedParameters) {
    this.namedParameters = namedParameters;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CommandArguments other = (CommandArguments) obj;
    if (namedParameters == null) {
      if (other.namedParameters != null)
        return false;
    } else if (!namedParameters.equals(other.namedParameters))
      return false;
    if (unnamedParameters == null) {
      if (other.unnamedParameters != null)
        return false;
    } else if (!unnamedParameters.equals(other.unnamedParameters))
      return false;
    return true;
  }

  public String getNamedParameter(String parameterName) {
    if (namedParameters == null || !namedParameters.containsKey(parameterName)) {
      throw new IllegalArgumentException("Missing argument '" + parameterName
          + "'.");
    }
    return namedParameters.get(parameterName);
  }

  public String getNamedParameterIfExists(String parameterName) {
    if (namedParameters == null || !namedParameters.containsKey(parameterName)) {
      return null;
    }
    return namedParameters.get(parameterName);
  }

  public String getUnnamedParameter(int argNo) {
    if (unnamedParameters == null || argNo > unnamedParameters.size()) {
      throw new IllegalArgumentException("Missing argument no. " + argNo + ".");
    }
    return unnamedParameters.get(argNo - 1);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((namedParameters == null) ? 0 : namedParameters.hashCode());
    result = prime * result
        + ((unnamedParameters == null) ? 0 : unnamedParameters.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "CommandArguments [unnamedParameters=" + unnamedParameters
        + ", namedParameters=" + namedParameters + "]";
  }

}
