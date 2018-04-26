package command;

import utility.ParseService;
import grid.Grid;
import grid.axis.Axes;
import grid.export.ExportService;
import grid.load.ImportService;
import io.log.Log;
import io.parse.CommandArguments;

import com.google.inject.Inject;

import config.Model;

public class LoadCommand implements Command {

  private final ImportService importService;
  private final Model model;
  private final Log log;
  private final ExportService exportService;
  private final ParseService parseService;

  @Inject
  public LoadCommand(ImportService importService, ExportService exportService,
      Model model, Log log, ParseService parseService) {
    this.importService = importService;
    this.exportService = exportService;
    this.model = model;
    this.log = log;
    this.parseService = parseService;
  }

  @Override
  public String getCommandName() {
    return "load";
  }

  @Override
  public void execute(CommandArguments commandArguments) {
    log.printInformationMessage("Running " + getCommandName() + " command");
    
    // parse one-time arguments
    String loadPath = commandArguments.getNamedParameter("load-path");
    String logTransformArg = commandArguments
        .getNamedParameterIfExists("log-transform");
    String exportPath = commandArguments
        .getNamedParameterIfExists("export-path");

    boolean logTransform = false;
    if (logTransformArg != null) {
      logTransform = parseService.parseBoolean(logTransformArg);
    }
    
    // get saved configuration
    double rotation = model.getRotationRadians();

    // check if file is accessible
    if (!importService.canAccessFile(loadPath)) {
      log.printErrorMessage("Could not load file.");
      return;
    }

    // load axes format from file
    Axes axes = importService.createAxesFromFile(loadPath, rotation);
    if (axes == null) {
      return; // error in making axes; details already logged.
    }
    log.printInformationMessage("Regular axes created.");
    log.printInformationMessage(axes);

    // grid data according to axes
    Grid<Double> grid = importService.createGridFromFile(loadPath, axes,
        logTransform, rotation);
    log.printInformationMessage("Points gridded according to regular axes.");
    log.printInformationMessage(grid);

    // save data and print summary
    model.saveAxes(axes);
    model.saveDataGrid(grid);
    
    if (exportPath != null) {
      exportService.export(grid, axes, rotation, exportPath, "data");
    }
  }

}
