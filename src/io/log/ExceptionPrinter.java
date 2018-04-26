package io.log;

import com.google.inject.Inject;

public class ExceptionPrinter {

  private final Log log;

  @Inject
  public ExceptionPrinter(Log log) {
    this.log = log;
  }

  public void logException(Exception e) {
    String stackTrace = "";
    for (StackTraceElement el :  e.getStackTrace()) {
      stackTrace += "\t" + el.toString();
      stackTrace += System.lineSeparator();
    }
    log.printErrorMessage(e.toString() + System.lineSeparator() + stackTrace);
  }

}
