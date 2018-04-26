package grid.load;

import static grid.axis.Dimension.X;
import static grid.axis.Dimension.Y;
import static grid.axis.Dimension.Z;
import grid.Grid;
import grid.axis.Axes;
import grid.axis.Axis;
import grid.axis.AxisService;
import grid.axis.Dimension;
import grid.axis.RotationService;
import io.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import utility.BufferService;

import com.google.inject.Inject;

public class ImportService {

  private final AxisService axisService;
  private final BufferService bufferService;
  private final Log log;
  private final RotationService rotationService;

  @Inject
  public ImportService(BufferService bufferService, Log log,
      AxisService axisService, RotationService rotationService) {
    this.bufferService = bufferService;
    this.log = log;
    this.axisService = axisService;
    this.rotationService = rotationService;
  }

  public boolean canAccessFile(String path) {
    File file = new File(path);
    return file.exists() && file.canRead();
  }

  public Axes createAxesFromFile(String path, double rotation) {
    try {
      BufferedReader bufferedReader = bufferService.getReadBuffer(path);
      Axes axes = createAxes(bufferedReader, rotation);
      bufferedReader.close();
      return axes;
    } catch (IOException e) {
      log.printErrorMessage("Error loading file: '" + e.getMessage() + "'.");
    }
    return null;
  }

  public Grid<Double> createGridFromFile(String path, Axes axes,
      boolean logTransform, double rotation) {
    try {
      BufferedReader bufferedReader = bufferService.getReadBuffer(path);
      Grid<Double> doubleGrid = loadPointsIntoGrid(bufferedReader,
          logTransform, axes, rotation);
      bufferedReader.close();
      return doubleGrid;
    } catch (IOException e) {
      log.printErrorMessage("Error loading file: '" + e.getMessage() + "'.");
    }
    return null;
  }

  private Axes createAxes(BufferedReader bufferedReader, double rotation) throws IOException {
    String header = bufferedReader.readLine();
    if (errorInHeader(header)) {
      return null; // error logged in method
    }

    // Sort ticks
    TreeSet<Double> xTicks = new TreeSet<Double>();
    TreeSet<Double> yTicks = new TreeSet<Double>();
    TreeSet<Double> zTicks = new TreeSet<Double>();

    String line = bufferedReader.readLine();
    int lineNumber = 2;
    while (line != null) {
      String[] lineSplit = line.split(",");

      // check number of entries okay
      int numLineFields = lineSplit.length;
      if (numLineFields != 4) {
        logErrorMessage("Row had " + numLineFields + " fields (expected 4).",
            line, lineNumber);
        return null;
      }

      // xyz components
      try {
        double x = Double.parseDouble(lineSplit[0]);
        double y = Double.parseDouble(lineSplit[1]);
        double z = Double.parseDouble(lineSplit[2]);

        double xReg = rotationService.xOriginalToRegular(x, y, rotation);
        double yReg = rotationService.yOriginalToRegular(x, y, rotation);

        xTicks.add(xReg);
        yTicks.add(yReg);
        zTicks.add(z);

      } catch (NumberFormatException e) {
        logErrorMessage("Unreadable or missing value.", line, lineNumber);
        return null;
      }

      // prepare for next line
      line = bufferedReader.readLine();
      lineNumber++;
    }

    Axis xAxis = axisService.createRegularAxis(xTicks, X);
    if (xAxis == null)
      return null;
    Axis yAxis = axisService.createRegularAxis(yTicks, Y);
    if (yAxis == null)
      return null;
    Axis zAxis = axisService.createRegularAxis(zTicks, Z);
    if (zAxis == null)
      return null;

    EnumMap<Dimension, Axis> axesMap = new EnumMap<Dimension, Axis>(
        Dimension.class);
    axesMap.put(X, xAxis);
    axesMap.put(Y, yAxis);
    axesMap.put(Z, zAxis);

    return new Axes(axesMap);
  }

  private Grid<Double> loadPointsIntoGrid(BufferedReader bufferedReader,
      boolean logTransform, Axes axes, double rotation) throws IOException {
    // skip attribute name (not read at this point)
    bufferedReader.readLine();

    Double[][][] array = axes.createNullArray(log);

    // record coordinates, calculate min attb value, and then make points
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    String line = bufferedReader.readLine();
    while (line != null) {
      String[] lineSplit = line.split(",");

      // xyz components
      double x = Double.parseDouble(lineSplit[0]);
      double y = Double.parseDouble(lineSplit[1]);
      double z = Double.parseDouble(lineSplit[2]);

      double xReg = rotationService.xOriginalToRegular(x, y, rotation);
      double yReg = rotationService.yOriginalToRegular(x, y, rotation);

      // values
      double a = Double.parseDouble(lineSplit[3]);
      min = Math.min(min, a);
      max = Math.max(max, a);

      // set value
      axes.setValueInArray(a, xReg, yReg, z, array);

      // prepare for next line
      line = bufferedReader.readLine();
    }
    
    // validate that the deepest z-index has a value
    // so long as the entire column isn't null
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        if (array[i][j][0] == null) {
          // check rest of column
          for (int k = 1; k < array[i][j].length; k++) {
            if(array[i][j][k] != null) {
              log.printErrorMessage("Data has missing at bottom slice (i=" + i + ", j=" + j + ")");
              return null;                 
            }
          }    
        }
      }
    }

    // apply log transformation
    if (logTransform) {
      if (min < 0) {
        log.printErrorMessage("Data negative, cannot apply log transform.");
        return null;
      }
      log.printInformationMessage("Applying natural logarithm to data");
      min = Math.log(min);
      max = Math.log(max);
      for (int i = 0; i < array.length; i++) {
        for (int j = 0; j < array[i].length; j++) {
          for (int k = 0; k < array[i][j].length; k++) {
            if (array[i][j][k] != null) {            
              array[i][j][k] = Math.log(array[i][j][k]);
            }
          }
        }
      }
    }
    
    // create fill volume
    Double[][][] fillVolume = axes.createNullArray(log);
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        for (int k = 0; k < array[i][j].length; k++) {
          fillVolume[i][j][k] = array[i][j][k];
        }
      }
    }
    
    // get null columns
    HashSet<Vector<Integer>> nullColumns = new HashSet<Vector<Integer>>();
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        if (array[i][j][0] == null) {
          // is a null column due to validation early
          Vector<Integer> v = new Vector<Integer>(2);
          v.add(i);
          v.add(j);
          nullColumns.add(v);
        }
      }
    }
    
    // generate boundary columns
    HashSet<Vector<Integer>> boundaryColumns = new HashSet<Vector<Integer>>();
    for (Vector<Integer> nullVec : nullColumns) {
      // save the 3x3x3 locations still within grid
      int iNull = nullVec.get(0);
      int jNull = nullVec.get(1);
      for (int ii = iNull - 1; ii <= iNull + 1; ii++) {
        if (ii < 0 || ii >= fillVolume.length) {
          continue;
        }
        for (int jj = jNull - 1; jj <= jNull + 1; jj++) {
          if (jj < 0 || jj >= fillVolume[ii].length) {
            continue;
          }          
          // within range, create potential boundary vector
          Vector<Integer> boundaryVec = new Vector<Integer>(2);
          boundaryVec.add(ii);
          boundaryVec.add(jj);
          boundaryColumns.add(boundaryVec);
        }
      }
    }
    // keep all 3x3x3 locations that aren't null columns already
    boundaryColumns.removeAll(nullColumns);
    
    // map each null column to its closest boundary column's value
    for (Vector<Integer> nullVec : nullColumns) {

      Integer iNull = nullVec.get(0);
      Integer jNull = nullVec.get(1);

      Vector<Integer> closest = null;
      double closestDistSq = Double.MAX_VALUE;

      // find boundary closest to (iNull, jNull)
      for (Vector<Integer> boundaryVec : boundaryColumns) {
        
        Integer iBoundary = boundaryVec.get(0);
        Integer jBoundary = boundaryVec.get(1);
        double distSq = (iNull - iBoundary) * (iNull - iBoundary)
            + (jNull - jBoundary) * (jNull - jBoundary);
        
        if (closest == null || distSq < closestDistSq) {
          closest = boundaryVec;
          closestDistSq = distSq;
        }
      }
      
      // now that we have a boundary column fill at corresponding zs
      Integer iBoundary = closest.elementAt(0);
      Integer jBoundary = closest.elementAt(1);
      for (int k = 0; k < fillVolume[iNull][jNull].length; k++) {
        fillVolume[iNull][jNull][k] = fillVolume[iBoundary][jBoundary][k];
      }
    }
    
    // finally, replace remaining nulls from the bottom up
    for (int i = 0; i < fillVolume.length; i++) {
      for (int j = 0; j < fillVolume[i].length; j++) {
        for (int k = 1; k < fillVolume[i][j].length; k++) {
          if (fillVolume[i][j][k] == null) {
            fillVolume[i][j][k] = fillVolume[i][j][k-1];
          }
        }
      }
    }
    
    return axes.createGrid(array, fillVolume);
  }

  private boolean errorInHeader(String header) {
    if (header == null) {
      log.printErrorMessage("File was empty.");
      return true;
    }

    // process header (non-empty, alphabetic)
    String[] headerSplit = header.split(",", -1);
    int numHeaderFields = headerSplit.length;
    for (int i = 0; i < numHeaderFields; i++) {
      headerSplit[i] = headerSplit[i].trim().toLowerCase();
      if (!headerSplit[i].matches("[a-zA-Z]{1,}")) {
        logErrorMessage("Header fields must be alphabetic and non-empty.",
            header, 1);
        return true;
      }
    }

    // check that spatial components are present
    if (!headerSplit[0].equals("x") || !headerSplit[1].equals("y")
        || !headerSplit[2].equals("z")) {
      logErrorMessage("First three header fields must be x, y, and z.", header,
          1);
      return true;
    }

    // check that there are no duplicated headers
    HashSet<String> tempSet = new HashSet<String>(Arrays.asList(headerSplit));
    if (tempSet.size() != numHeaderFields) {
      logErrorMessage("Duplicate header fields.", header, 1);
      return true;
    }

    // check that there is one attribute (non-spatial)
    if (headerSplit.length != 4) {
      logErrorMessage("One non-spatial attribute required.", header, 1);
      return true;
    }

    return false;
  }

  private void logErrorMessage(String errorMessage, String line, int lineNumber) {
    log.printErrorMessage(errorMessage + " Line " + lineNumber + ": '" + line
        + "'.");
  }
}
