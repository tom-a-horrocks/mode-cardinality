package io.parse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import io.parse.CommandArguments;
import io.parse.CommandParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CommandParserTest {

  private CommandParser commandParser;
  
  @Mock private CommandMapper commandMapper;

  @Before
  public void setUp() throws Exception {   
    initMocks(this);
    commandParser = new CommandParser(commandMapper);
  }

  @Test
  public void commandNameIsGivenToService() {
    String argWithNameOnly = "load";

    commandParser.getCommandService(argWithNameOnly);
    
    verify(commandMapper).getCommand("load");
  }
  
  @Test
  public void argumentWithNoParametersHasEmptyCommandArguments() {
    String argWithNameOnly = "load";

    CommandArguments args = commandParser.getCommandArguments(argWithNameOnly);
    
    assertThat(args, is(new CommandArguments()));
  }

  @Test
  public void argumentWithUnquotedParametersParses() {
    String argWithUnquotedParameters = "load C:\\data_dir\\data.csv";

    CommandArguments args = commandParser.getCommandArguments(argWithUnquotedParameters);

    assertThat(args.getUnnamedParameter(1), is("C:\\data_dir\\data.csv"));
  }

  @Test
  public void argumentWithQuotedParametersParses() {
    String argWithQuotedParameters = "load \"C:\\data dir\\data.csv\"";

    CommandArguments args = commandParser.getCommandArguments(argWithQuotedParameters);
    assertThat(args.getUnnamedParameter(1), is("C:\\data dir\\data.csv"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void argumentWithUnbalancedQuotesException() throws IllegalArgumentException {
    String argWithUnbalancedQuotes = "load \"C:\\data dir\\data.csv";

    commandParser.getCommandArguments(argWithUnbalancedQuotes);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyArgumentsException() throws IllegalArgumentException {
    commandParser.getCommandArguments("");
  }
  
  @Test
  public void argumentWithNamedParametersIsParsed() {
    String argWithNamedParameters = "load --param1 val1 --param2 val2";
    CommandArguments parsed = commandParser.getCommandArguments(argWithNamedParameters);
    
    assertThat(parsed.getNamedParameter("param1"), is("val1"));
    assertThat(parsed.getNamedParameter("param2"), is("val2"));
  }
  
  @Test
  public void argumentWithQuotedNamedParametersParses() {
    String argWithQuotedParameters = "load --paramname \"C:\\data dir\\data.csv\"";

    CommandArguments parsed = commandParser.getCommandArguments(argWithQuotedParameters);

    assertThat(parsed.getNamedParameter("paramname"), is("C:\\data dir\\data.csv"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void argumentWithNamedParameterAndMissingValue() {
    commandParser.getCommandArguments("load --param1 --param2 something");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void argumentWithNamedParameterAndThenStringEnds() {
    commandParser.getCommandArguments("load --param1");
  }

}
