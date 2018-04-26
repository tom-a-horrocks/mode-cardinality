package command;

import grid.mask.EllipsoidRadius;
import io.log.Log;
import io.parse.CommandArguments;

import java.util.Arrays;

import com.google.inject.Inject;

import config.Model;

public class SetOptionCommand implements Command {

  private final Model model;
  private final Log log;

  @Inject
  public SetOptionCommand(Model model, Log log) {
    this.model = model;
    this.log = log;
  }

  @Override
  public String getCommandName() {
    return "set-option";
  }

  @Override
  public void execute(CommandArguments commandArguments) {
    log.printInformationMessage("Running " + getCommandName() + " command");
    
    String radiusArg = commandArguments
        .getNamedParameterIfExists("local-radius");

    String minimalExportArg = commandArguments
        .getNamedParameterIfExists("minimal-export");
    String attributeBandwidthSelectorString = commandArguments
        .getNamedParameterIfExists("attribute-bandwidth-selector");
    String projectionBandwidthSelectorString = commandArguments
        .getNamedParameterIfExists("projection-bandwidth-selector");    
    String rotationArg = commandArguments
        .getNamedParameterIfExists("rotation");
    String bandwidthMultiplierArg = commandArguments
        .getNamedParameterIfExists("bandwidth-multiplier");

    if (allNull(minimalExportArg, radiusArg, attributeBandwidthSelectorString,
        projectionBandwidthSelectorString, rotationArg, bandwidthMultiplierArg)) {
      throw new IllegalArgumentException("Expecting at least one option");
    }
    if (minimalExportArg != null) {
      if (minimalExportArg.equalsIgnoreCase("true")) {
        model.saveMinimalExport(true);
      } else if (minimalExportArg.equalsIgnoreCase("false")) {
        model.saveMinimalExport(false);
      } else {
        throw new IllegalArgumentException(
            "minimal-export must be followed by \"true\" or \"false\"");
      }
    }
    
    if (radiusArg != null) {
      double[] axesRadii = Arrays
          .stream(radiusArg.split(","))
          .mapToDouble(s -> Double.parseDouble(s))
          .toArray();
      if (axesRadii.length == 1) {
        model.saveLocalRadius(new EllipsoidRadius(axesRadii[0]));
      } else if (axesRadii.length == 3) {
        model.saveLocalRadius(new EllipsoidRadius(axesRadii));
      } else {
        throw new IllegalArgumentException("local-radius must be one or three elements, was '" + radiusArg + "'");
      }
    }

    if (attributeBandwidthSelectorString != null) {
      try {
        // a number is a valid input
        Double.parseDouble(attributeBandwidthSelectorString);
        model.saveAttributeBandwidthSelectorString(attributeBandwidthSelectorString);
      } catch (NumberFormatException e) {
        // else check if string is right format
        if (attributeBandwidthSelectorString.equals("silverman")) {
          model.saveAttributeBandwidthSelectorString(attributeBandwidthSelectorString);
        } else {
          throw new IllegalArgumentException("The bandwidth selector '"
              + attributeBandwidthSelectorString + "' does not exist.");
        }
      }
    }
    
    if (projectionBandwidthSelectorString != null) {
      try {
        // a number is a valid input
        Double.parseDouble(projectionBandwidthSelectorString);
        model.saveProjectionBandwidthSelectorString(projectionBandwidthSelectorString);
      } catch (NumberFormatException e) {
        // else check if string is right format
        if (projectionBandwidthSelectorString.equals("silverman")) {
          model.saveProjectionBandwidthSelectorString(projectionBandwidthSelectorString);
        } else {
          throw new IllegalArgumentException("The bandwidth selector '"
              + projectionBandwidthSelectorString + "' does not exist.");
        }
      }
    }
    
    if (rotationArg != null) {
      model.saveRotation(Double.parseDouble(rotationArg));
    }
    
    if (bandwidthMultiplierArg != null) {
      model.saveBandwidthMultiplier(Double.parseDouble(bandwidthMultiplierArg));
    }
  }

  private boolean allNull(Object... objects) {
    for (Object object : objects) {
      if (object != null) {
        return false;
      }
    }
    return true;
  }

}
