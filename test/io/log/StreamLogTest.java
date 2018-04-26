package io.log;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import io.log.Log;
import io.log.StreamLog;

import java.io.PrintStream;

import org.junit.Test;
import org.mockito.Mockito;

public class StreamLogTest {

  @Test
  public void statusLogPrintsInformationUpdateVerbatim() {
    PrintStream printStream = mock(PrintStream.class);

    Log streamLog = new StreamLog(printStream);
    streamLog.printInformationMessage("Status message.");
    
    verify(printStream).println("Status message.");
  }
  
  @Test
  public void TextViewPrintsErrorMessageWithErrorInMessage() {
    PrintStream printStream = mock(PrintStream.class);

    Log streamLog = new StreamLog(printStream);
    streamLog.printErrorMessage("message.");
    
    verify(printStream).println(Mockito.contains("ERROR"));
  }
  
  @Test
  public void TextViewPrintsWarningMessageWithWarningInMessage() {
    PrintStream printStream = mock(PrintStream.class);

    Log streamLog = new StreamLog(printStream);
    streamLog.printWarningMessage("message.");
    
    verify(printStream).println(Mockito.contains("WARNING"));
  }

  
}
