package io.log;

import java.io.PrintStream;

public class StreamLog implements Log {

  private PrintStream printStream;

  public StreamLog(PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void printErrorMessage(String message) {
    printStream.println("ERROR:\t" + message);
  }

  @Override
  public void printInformationMessage(String message) {
    printStream.println(message);
  }

  @Override
  public void printInformationMessage(Object message) {
    printInformationMessage(message.toString());
  }

  @Override
  public void printWarningMessage(String message) {
    printStream.println("WARNING:\t" + message);
  }

}
