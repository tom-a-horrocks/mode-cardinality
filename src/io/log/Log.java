package io.log;

public interface Log {

  public void printErrorMessage(String message);

  public void printInformationMessage(String message);
  
  public void printInformationMessage(Object message);

  public void printWarningMessage(String string);

}