package io;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.CommandLineInterface;
import io.log.ExceptionPrinter;
import io.parse.CommandArguments;
import io.parse.CommandParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import command.Command;

public class CommandLineInterfaceTest {

  @Mock private CommandParser commandParser;
  @Mock private ExceptionPrinter exceptionPrinter;
  @Mock private CommandArguments commandArguments;
  @Mock private Command command;
  
  CommandLineInterface cli;
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    cli = new CommandLineInterface(commandParser, exceptionPrinter);
  }
  
  @Test
  public void commandServiceIsCreatedAndExecutedOnCommandArguments() {
    when(commandParser.getCommandService("some args"))
      .thenReturn(command);
    
    when(commandParser.getCommandArguments("some args"))
      .thenReturn(commandArguments);
    
    cli.parseArgumentsAndExecuteCommand("some args");

    verify(command).execute(commandArguments);
  }

  @Test
  public void errorIsLoggedWhenErrorInBuildingOrExecutingCommand() throws Exception {
    IllegalArgumentException e = new IllegalArgumentException("Descriptive Message");
    
    when(commandParser.getCommandService(any()))
      .thenReturn(command);
    doThrow(e).
      when(command).execute(any());

    cli.parseArgumentsAndExecuteCommand("some args");

    verify(exceptionPrinter).logException(e);
  }
}


