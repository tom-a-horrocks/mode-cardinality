package config;

import grid.Grid;
import grid.axis.Axes;
import grid.mask.EllipsoidRadius;
import io.log.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Model {

  // data
  private Grid<Double> dataGrid;
  private Axes axes;

  // options
  private boolean minimalExport;
  private EllipsoidRadius ellipsoidRadius;
  private double rotation = 0;
  private double bandwidthMultiplier = 1.0;
  private String attributeBandwidthSelectorString = "silverman";
  private String projectionBandwidthSelectorString = "silverman";
  
  // collaborators
  private final Log log;

  @Inject
  public Model(Log log) {
    this.log = log;
  }

  public void saveDataGrid(Grid<Double> dataGrid) {
    this.dataGrid = dataGrid;
    log.printInformationMessage("Point Grid saved.");
  }

  public void saveAxes(Axes axes) {
    this.axes = axes;
    log.printInformationMessage("Axes saved.");
  }

  public void saveMinimalExport(boolean minimalExport) {
    this.minimalExport = minimalExport;
    log.printInformationMessage("Minimal Export option set: " + this.minimalExport);
  }

  public void saveLocalRadius(EllipsoidRadius ellipsoidRadius) {
    this.ellipsoidRadius = ellipsoidRadius;
    log.printInformationMessage("Local radius set: " + this.ellipsoidRadius);
  }

  public void saveRotation(double rotation) {
    this.rotation = rotation;
    log.printInformationMessage("Rotation set: " + this.rotation);
  }
  
  public void saveBandwidthMultiplier(double parseDouble) {
    this.bandwidthMultiplier = parseDouble;
    log.printInformationMessage("Bandwidth multiplier set: " + this.bandwidthMultiplier);
  }

  public void saveAttributeBandwidthSelectorString(String attributeBandwidthSelectorString) {
    this.attributeBandwidthSelectorString = attributeBandwidthSelectorString;
    log.printInformationMessage("Attribute bandwidth selector string: " + this.attributeBandwidthSelectorString);
  }
  
  public void saveProjectionBandwidthSelectorString(String projectionBandwidthSelectorString) {
    this.projectionBandwidthSelectorString = projectionBandwidthSelectorString;
    log.printInformationMessage("Projection bandwidth selector string: " + this.projectionBandwidthSelectorString);
  }

  public Grid<Double> getDataGrid() {
    nullCheck(dataGrid, "Data Grid");
    return dataGrid;
  }

  public Axes getAxes() {
    nullCheck(axes, "Axes");
    return axes;
  }

  public double getRotationRadians() {
    return Math.PI * rotation / 180;
  }

  public EllipsoidRadius getLocalRadius() {
    nullCheck(ellipsoidRadius, "Local Radius");
    return ellipsoidRadius;
  }
  
  // These getter methods only used by providers

  protected boolean getMinimalExport() {
    return minimalExport;
  }
  
  protected String getAttributeBandwidthSelectorString() {
    return attributeBandwidthSelectorString;
  }
  
  protected String getProjectionBandwidthSelectorString() {
    return projectionBandwidthSelectorString;
  }
  protected double getBandwidthMultiplier() {
    return bandwidthMultiplier;
  }

  private void nullCheck(Object obj, String name) {
    if (obj == null) {
      throw new RuntimeException("Retrieving null " + name);
    }
  }

}
