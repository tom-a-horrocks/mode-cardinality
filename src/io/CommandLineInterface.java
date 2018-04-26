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

  private final static String me = "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@XB?%\"\"?\"..?%@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%M..... .  ....  . .\"**%%@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%B .....    .<. .  . ..   .....?%@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@%\" : .... ...--= --~......-.    ...:.@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@M....-.-.  --=.~qNoWEM=WE<qq~--~.... - .@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@L.  .- .  .:<====E=MM===qbMb=EEWWqqqq.. .[@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@E .... :   :<=====E===WMMMEWMMMMMMMM=qqp %@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@'- .-  --  .-=====W=MEWM##MMMMMMMMMMMMMMMp.@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@M  ..       .--<<====WMMMMMEEMMMMMMMMMMMMME/J@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@M ..      .   .->>MW===MMMM=EMMMM@MM@MMMMM=WM.@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@L .     .     :S=><=W===---....-==MMMMMMMMMMMJ@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@D.  ~G=.    ..-======@@MM====MMqqqM#MMMMM====J@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@P  #-=>MW..  .:====NMMM=--:3#M=====#MMM==WWj/J@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@]  =-oMM=q-  .=====MMEMMMMMW<~qo=>==E=  ,j.=@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  ?=W=~W=W--.:===EME=WWWqEWWMMM=MW=WEMqW-qg@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  `=W~===...-====WWqWEW@MMMMWEM====WME=NE@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@    MWMM  . .-=====WMNMMMMM===WVW=WNM=NMW@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@   =EG.. .:-=====WSMMM==M=E=====SMM=MNMj@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,  (=<=  . :-===EEWE===-.--:-==<=====NM@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@,.>==. .....--===.:-=--.--~~Wo~---====@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@F===~.. .....-.=-~qq-=RM@@#M@~L .:<=:@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@.=== -     . .-=-SMM=M=W:=MMM=.~-==.@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@]-===-       .--==NMMMEEM===VW==== .@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@.-=<==~..      ...>MVMMMM=====b=.. @@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@%=  -  .--===W=~.         .-:====qWE====..@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@E#@...-=<======E>           . .--======...@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@@M#@M@@W -=====WEq===~..      .   .....-- . @@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@@@@M7QM@@@@#p-=====M=====G~..      .  . .   ,#@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@@@#BV@M#@@@@@@MM.==W==WM===<=<q~- ... .aa@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@@@@@#M<MMRM@@@@@@@@M@@.====WWW========WW=@@@@@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@@@@@M\"~<QQMM@Q=M@@M@@@MM@@@@:=======<=======>~\"@@@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@@@@@M\"-KMM==MMMMM@@Mo@@MMQ@E@@@@@M=-============>WQ=%@@@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@@@@@%\"<.=WMMEWMM=WMMMME>=.#M@@@@@M@Mgx#p--========.-==MM~X%@@@@@@@@@@@@@@@@"
      + "\n"
      + " @@@@@%M~===<W=(MMM#@WNWM`^ / @~.K@@M#WCdR&~~,,-<<======.=>=#W@@& \"@@@@@@@@@@@@@@"
      + "\n"
      + " @@@%;M=qMqM  @M` o=` ~.=~=~-`  ..?M @Lj@?M~~,~ =====<==-=~@>@##MM= `@@@@@@@@@@@@"
      + "\n"
      + " %E==<==<= ~=..~= .~@MW~3g/>~,.~q@@~(M #C( Wp.=Km:<=====-@=~@MMNM@MNQS@@@@@@@@@@@"
      + "\n"
      + " W>== d qM .~=-~Q@MEWMMMMM==WMgygy.,== @M @p =M<-=MG=<==:=NMMMM#Q#MWqM=@@@@@@@@@@"
      + "\n"
      + " Mq> =~=.=Qo/-~MMMMWW=WMME==-=WMM=MWM<M/M @MMM/==--@W--.>&KM@,7oq~EM<W@J@@@@@@@@@"
      + "\n"
      + " MM @M~.=p =WgC8WMMW=M#@=WMWMMMMWMEW=M==<M~^NM?p==SM===.@MMmp=@M@/MMEEMK@@@@@@@@@"
      + "\n"
      + " MW.^M #MMMMMMM@M=MM@@M=M>MMM@MM=NWMM=WWE=W@.?@ ~?N #L =qMEQW-=EqMWWEM7=@@@@@@@@@"
      + "\n"
      + " MM/  @M @MM=~M@M@q-?MM #MMMWM#MM#MMMENMEMWMMM @ \\ #.p$ MMM>MMEEEMEM=W =@@@@@@@@@"
      + "\n"
      + "  # /#= @MM@@MMWMEMQ  7M~UEMM #M.#WMMEMMMMMMM=@M@@M(<L  =L.0MQMMMEMNW i#S@@@@@@@@"
      + "\n"
      + "  =.o y@MMMMMM@MM==8Mp ^M 7EM @ (WMNVNWM@M@M==WMMMMM==q= MW==M LNMSM=M:#E@@@@@@@@"
      + "\n"
      + " ..6 #WEMMMM@@MMM=W. ^M =E.>CqM @MM (MMMMEMQQQ@M-S#MWWp`=#,?M?LL8MWNWMM>M@@@@@@@@"
      + "\n";

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
    System.out.println(me);
    printBannerLine();
    System.out.println("Welcome to the DENCLUE cluster program.");
    System.out.println(instructions());
    printBannerLine();
  }

}
