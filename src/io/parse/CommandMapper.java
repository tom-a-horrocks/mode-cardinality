package io.parse;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

import com.google.inject.Inject;

import command.BandwidthVolumeCommand;
import command.Command;
import command.LoadCommand;
import command.ModeVolumeCommand;
import command.SetOptionCommand;

public class CommandMapper {

  private final HashMap<String, Command> commands = new HashMap<String, Command>();

  @Inject
  public CommandMapper(LoadCommand loadCommand,
      BandwidthVolumeCommand bandwidthVolumeCommand,
      SetOptionCommand setOptionCommand, ModeVolumeCommand modeVolumeCommand) {
    // TODO inject all Commands by reflection or multibinding
    for (Command command : asList(loadCommand, bandwidthVolumeCommand,
        setOptionCommand, modeVolumeCommand)) {
      commands.put(command.getCommandName(), command);
    }
  }

  public Command getCommand(String name) {
    if (!commands.containsKey(name)) {
      throw new IllegalArgumentException("Unknown command '" + name + "'.");
    }
    return commands.get(name);
  }

  public List<String> getCommandNames() {
    return commands.keySet().stream().sorted().collect(Collectors.toList());
  }
}
