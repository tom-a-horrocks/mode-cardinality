package command;

import kde.bw.BandwidthSelector;
import utility.ParseService;
import grid.Grid;
import grid.axis.Axes;
import grid.export.ExportService;
import grid.mask.EllipsoidRadius;
import grid.mask.Mask;
import grid.mask.MaskService;
import grid.mask.MaskedGridIterator;
import grid.operator.ModeCountCellOperator;
import grid.operator.ModeCountCellOperatorArgs;
import grid.operator.SimpleModeCountCellOperator;
import grid.operator.SimpleModeCountCellOperatorArgs;
import io.log.Log;
import io.parse.CommandArguments;

import com.google.inject.Inject;

import config.AttributeBandwidthSelectorProvider;
import config.Model;
import config.ProjectionBandwidthSelectorProvider;

public class ModeVolumeCommand implements Command {

  private final MaskService maskService;
  private final ExportService exportService;
  private final ParseService parseService;

  private final Log log;

  private final AttributeBandwidthSelectorProvider attributeBandwidthSelectorProvider;
  private final ProjectionBandwidthSelectorProvider projectionBandwidthSelectorProvider;
  private final Model model;

  private final SimpleModeCountCellOperator simpleModeCountCellOperator;
  private final ModeCountCellOperator modeCountCellOperator;

  @Inject
  public ModeVolumeCommand(Log log, MaskService maskService,
      ExportService exportService,
      AttributeBandwidthSelectorProvider attributeBandwidthSelectorProvider,
      ProjectionBandwidthSelectorProvider projectionBandwidthSelectorProvider,
      ParseService parseService, Model model,
      SimpleModeCountCellOperator simpleModeCountCellOperator,
      ModeCountCellOperator modeCountCellOperator) {
    this.log = log;
    this.maskService = maskService;
    this.exportService = exportService;
    this.attributeBandwidthSelectorProvider = attributeBandwidthSelectorProvider;
    this.projectionBandwidthSelectorProvider = projectionBandwidthSelectorProvider;
    this.parseService = parseService;
    this.model = model;
    this.simpleModeCountCellOperator = simpleModeCountCellOperator;
    this.modeCountCellOperator = modeCountCellOperator;
  }

  @Override
  public String getCommandName() {
    return "mode-volume";
  }

  @Override
  public void execute(CommandArguments commandArguments) {
    log.printInformationMessage("Running " + getCommandName() + " command");

    // parse one-time arguments
    String outPath = commandArguments.getNamedParameter("export-path");

    String numSubDivisionsString = commandArguments
        .getNamedParameter("number-of-subdivisions");
    int numSubDivisions = Integer.parseInt(numSubDivisionsString);

    String usingGradientsArg = commandArguments
        .getNamedParameter("use-gradient");
    boolean usingGradients = parseService.parseBoolean(usingGradientsArg);
    
    String gradientCutoffArg = commandArguments
        .getNamedParameterIfExists("gradient-cutoff");
    double gradientCutoff = Double.NaN;
    if (usingGradients) {
      if (gradientCutoffArg == null) {
        throw new IllegalArgumentException("gradient-cutoff required when use-gradient is true");
      }
      gradientCutoff = Double.parseDouble(gradientCutoffArg);
    }

    // get saved configuration
    Grid<Double> pointGrid = model.getDataGrid();
    Axes axes = model.getAxes();
    EllipsoidRadius radius = model.getLocalRadius();
    BandwidthSelector attributeBandwidthSelector = attributeBandwidthSelectorProvider.get();
    double rotation = model.getRotationRadians();

    // create parameters
    Mask mask = maskService.createAccessMask(axes, radius);
    // DoG masked iterator needs to be placed onto the centre of the DoG
    MaskedGridIterator<Double[]> DoG = new MaskedGridIterator<Double[]>(
        maskService.createDerivativeOfGaussianFilterGrid(axes, radius), mask);
    DoG.resetAndChangePosition(mask.iMid(), mask.jMid(), mask.kMid());

    // calculate modes using appropriate cell operator
    Grid<?> modeGrid;
    String header = "num_modes";
    if (usingGradients) {
      header += " gradient_mag xGrad yGrad zGrad grad_used";
      BandwidthSelector projectionBandwidthSelector = projectionBandwidthSelectorProvider.get();
      modeGrid = pointGrid.applyPerCell(modeCountCellOperator,
          new ModeCountCellOperatorArgs(attributeBandwidthSelector, projectionBandwidthSelector, numSubDivisions, DoG, gradientCutoff, axes.getNormalisedScales()),
          mask, log);
      // report method statistics
      log.printInformationMessage("Proportion of cells that made use of the gradient: " + modeCountCellOperator.getPercentageOfCellsThatUsedGradient());
    } else {
      modeGrid = pointGrid.applyPerCell(simpleModeCountCellOperator,
          new SimpleModeCountCellOperatorArgs(attributeBandwidthSelector,
              numSubDivisions), mask, log);
    }

    // export results
    exportService.export(modeGrid, axes, rotation, outPath, header);
  }

}
