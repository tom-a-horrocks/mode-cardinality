package io.parse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import command.Command;

public class CommandParser {

  private final CommandMapper commandMapper;

  @Inject
  public CommandParser(CommandMapper commandMapper) {
    this.commandMapper = commandMapper;
  }

  public CommandArguments getCommandArguments(String args) {
    String[] splitArguments = validateAndSplitArgs(args);

    LinkedList<String> unnamedParameters = new LinkedList<String>();
    HashMap<String, String> namedParameters = new HashMap<String, String>();

    for (int i = 1; i < splitArguments.length; i++) {
      String arg = splitArguments[i];
      // check for named parameter
      if (arg.startsWith("--")) {
        String paramName = arg.substring(2, arg.length());
        if (i + 1 == splitArguments.length) {
          throw new IllegalArgumentException("Missing value for parameter '"
              + paramName + '"');
        }
        String paramValue = splitArguments[i + 1];
        if (paramValue.startsWith("--")) {
          throw new IllegalArgumentException("Missing value for parameter '"
              + paramName + '"');
        }
        paramValue = trimQuotesIfPresent(paramValue);
        namedParameters.put(paramName, paramValue);
        i++;
      } else {
        arg = trimQuotesIfPresent(arg);
        unnamedParameters.add(arg);
      }
    }

    return new CommandArguments(unnamedParameters, namedParameters);
  }

  public Command getCommandService(String args) {
    String name = validateAndSplitArgs(args)[0];
    return commandMapper.getCommand(name);
  }
  
  public List<String> getCommandNames() {
    return commandMapper.getCommandNames();
  }

  private String trimQuotesIfPresent(String arg) {
    if (arg.startsWith("\"") && arg.endsWith("\"")) {
      // trim off start and end quotes
      arg = arg.substring(1, arg.length() - 1);
    }
    return arg;
  }

  private String[] validateAndSplitArgs(String args) {
    // check for missing args
    if (args.isEmpty()) {
      throw new IllegalArgumentException("No arguments provided");
    }

    // Check for unbalanced quotes
    int quoteCount = 0;
    for (char c : args.toCharArray()) {
      if (c == '"') {
        quoteCount++;
      }
    }
    if (quoteCount % 2 != 0) {
      throw new IllegalArgumentException("Unbalanced quotes in '" + args + '"');
    }

    return args.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
  }

}
