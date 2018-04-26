package command;

import io.parse.CommandArguments;

public interface Command {
  
  public String getCommandName();
  
  public void execute(CommandArguments commandArguments);

}