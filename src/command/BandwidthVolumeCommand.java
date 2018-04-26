package command;

import kde.bw.BandwidthSelector;
import io.log.Log;
import io.parse.CommandArguments;
import grid.Grid;
import grid.axis.Axes;
import grid.export.ExportService;
import grid.mask.EllipsoidRadius;
import grid.mask.MaskService;
import grid.mask.Mask;
import grid.mask.MaskedGridIterator;
import grid.operator.AttributeBandwidthCellOperator;
import grid.operator.AttributeBandwidthCellOperatorArgs;
import grid.operator.ProjectionBandwidthCellOperator;
import grid.operator.ProjectionBandwidthCellOperatorArgs;

import com.google.inject.Inject;

import config.AttributeBandwidthSelectorProvider;
import config.Model;
import config.ProjectionBandwidthSelectorProvider;

public class BandwidthVolumeCommand implements Command {

  private final MaskService maskService;
  private final ExportService exportService;
  private final AttributeBandwidthCellOperator attributeBandwidthCellOperator;
  private final ProjectionBandwidthCellOperator projectionBandwidthCellOperator;
  private final Log log;
  private final Model model;
  private final AttributeBandwidthSelectorProvider attributeBandwidthSelectorProvider;
  private final ProjectionBandwidthSelectorProvider projectionBandwidthSelectorProvider;

  @Inject
  public BandwidthVolumeCommand(Log log, MaskService maskService,
      ExportService exportService,
      AttributeBandwidthCellOperator attributeBandwidthCellOperator,
      ProjectionBandwidthCellOperator projectionBandwidthCellOperator,
      Model model,
      AttributeBandwidthSelectorProvider attributeBandwidthSelectorProvider,
      ProjectionBandwidthSelectorProvider projectionBandwidthSelectorProvider) {
    this.log = log;
    this.maskService = maskService;
    this.exportService = exportService;
    this.attributeBandwidthCellOperator = attributeBandwidthCellOperator;
    this.projectionBandwidthCellOperator = projectionBandwidthCellOperator;
    this.model = model;
    this.attributeBandwidthSelectorProvider = attributeBandwidthSelectorProvider;
    this.projectionBandwidthSelectorProvider = projectionBandwidthSelectorProvider;
  }

  @Override
  public String getCommandName() {
    return "bandwidth-volume";
  }

  @Override
  public void execute(CommandArguments commandArguments) {
    log.printInformationMessage("Running " + getCommandName() + " command");

    // parse one-time arguments
    String attributeExportPath = commandArguments
        .getNamedParameter("attribute-export-path");
    String projectionExportPath = commandArguments
        .getNamedParameterIfExists("projection-export-path");

    // get saved configuration
    Grid<Double> dataGrid = model.getDataGrid();
    Axes axes = model.getAxes();
    EllipsoidRadius ellipsoidRadius = model.getLocalRadius();
    BandwidthSelector bandwidthSelector = attributeBandwidthSelectorProvider
        .get();
    double rotation = model.getRotationRadians();

    // create parameters
    Mask mask = maskService.createAccessMask(axes, ellipsoidRadius);
    AttributeBandwidthCellOperatorArgs aArgs = new AttributeBandwidthCellOperatorArgs(
        bandwidthSelector);

    // calculate attribute bandwidth
    Grid<Double> bandwidthGrid = dataGrid.applyPerCell(
        attributeBandwidthCellOperator, aArgs, mask, log);

    // export results
    exportService.export(bandwidthGrid, axes, rotation, attributeExportPath,
        "attribute-bandwidth");

    if (projectionExportPath != null) {
      // create DoG for gradient calculation
      MaskedGridIterator<Double[]> DoG = new MaskedGridIterator<Double[]>(
          maskService.createDerivativeOfGaussianFilterGrid(axes, ellipsoidRadius), mask);
      DoG.resetAndChangePosition(mask.iMid(), mask.jMid(), mask.kMid());

      // get bandwidth selector
      bandwidthSelector = projectionBandwidthSelectorProvider.get();
      ProjectionBandwidthCellOperatorArgs pArgs = new ProjectionBandwidthCellOperatorArgs(
          bandwidthSelector, DoG, axes.getNormalisedScales());
      bandwidthGrid = dataGrid.applyPerCell(projectionBandwidthCellOperator,
          pArgs, mask, log);

      exportService.export(bandwidthGrid, axes, rotation, projectionExportPath,
          "projection-bandwidth");
    }
  }

}
