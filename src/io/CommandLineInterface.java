package io;

import io.log.ExceptionPrinter;
import io.parse.CommandArguments;
import io.parse.CommandParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Inject;

import command.Command;
import config.ClusteringModule;

public class CommandLineInterface {

  private final static String me = "";

  private final CommandParser commandParser;
  private final ExceptionPrinter exceptionPrinter;

  @Inject
  public CommandLineInterface(CommandParser commandParser,
      ExceptionPrinter exceptionPrinter) {
    this.commandParser = commandParser;
    this.exceptionPrinter = exceptionPrinter;
  }

  public static void main(String[] args) {
    Guice.createInjector(new ClusteringModule())
        .getInstance(CommandLineInterface.class)
        .run(args);
  }

  public void run(String[] args) {
    welcomeMessage();

    if (args.length == 1) {
      // Run in script mode
      scriptMessage();
      String path = args[0];
      try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = br.readLine()) != null) {
          line = line.trim();
          System.out.println("Executing '" + line + "'...");
          parseArgumentsAndExecuteCommand(line);
          printBannerLine();
        }
      } catch (Exception e) {
        exceptionPrinter.logException(e);
      }
    } else {
      // Read in commands
      Scanner scanner = new Scanner(System.in);
      String arg = scanner.nextLine();
      while (!arg.equals("exit")) {
        if (!arg.trim().isEmpty()) {
          parseArgumentsAndExecuteCommand(arg);
          printWithinBanner(instructions());
        }
        arg = scanner.nextLine();
      }
      scanner.close();
    }
  }
  
  /**
   * Executes this programming with the given arguments.
   * 
   * @param args
   *          arguments from the command line.
   * @return execution status
   */
  public void parseArgumentsAndExecuteCommand(String args) {
    // Parse arguments and give to Command Factory
    try {
      Command command = commandParser.getCommandService(args);
      CommandArguments arguments = commandParser.getCommandArguments(args);
      command.execute(arguments);
    } catch (Exception e) {
      exceptionPrinter.logException(e);
    }
  }

  /**
   * Printing helper functions.
   */

  private void printBannerLine() {
    System.out.println("-----------------------------------------------------");
  }

  private String instructions() {
    List<String> commandNames = commandParser.getCommandNames();
    String message = "Commands are ";
    for (int i = 0; i < commandNames.size() - 1; i++) {
      message += "'" + commandNames.get(i) + "', ";
    }
    message += "or '" + commandNames.get(commandNames.size() - 1) + "'.";
    message += System.lineSeparator() +  "Type 'exit' to exit.";
    return message;
  }

  private void printWithinBanner(String... linesToPrint) {
    printBannerLine();
    Arrays.asList(linesToPrint).forEach(l -> System.out.println(l));
    printBannerLine();
  }

  private void scriptMessage() {
    printWithinBanner("Script mode engaged.");
  }

  private void welcomeMessage() {
    printBannerLine();
    System.out.println("Welcome to the SI-MC edge detection program.");
    System.out.println(instructions());
    printBannerLine();
  }

}
