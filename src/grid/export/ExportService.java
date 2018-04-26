package grid.export;

import grid.Grid;
import grid.GridIterator;
import grid.axis.Axes;
import grid.axis.RotationService;
import io.log.Log;

import java.io.BufferedWriter;
import java.io.IOException;

import utility.BufferService;
import static grid.axis.Dimension.*;

import com.google.inject.Inject;

import config.LineMapperProvider;

public class ExportService {

  private static final String DELIM = " ";
  
  private final BufferService bufferService;
  private final RotationService rotationService;
  private final Log log;
  private final LineMapperProvider lineMapperProvider;

  @Inject
  public ExportService(BufferService bufferService,
      RotationService rotationService, LineMapperProvider lineMapperProvider,
      Log log) {
    this.bufferService = bufferService;
    this.rotationService = rotationService;
    this.lineMapperProvider = lineMapperProvider;
    this.log = log;
  }

  public void export(Grid<?> grid, Axes axes, double rotation, String path, String dataHeader) {
    LineMapper lineMapper = lineMapperProvider.get();
    try {
      BufferedWriter bufferedWriter = bufferService.getWriteBuffer(path);
      // header
      String preHeader = lineMapper.nonDataHeader() == null
          || lineMapper.nonDataHeader().isEmpty() ? "" : lineMapper
          .nonDataHeader() + DELIM;
      writeLine(bufferedWriter, preHeader + dataHeader);
      // body
      GridIterator<?> gridIterator = grid.fullGridIterator();
      while (gridIterator.hasNext()) {
        int i = gridIterator.getNextI();
        int j = gridIterator.getNextJ();
        int k = gridIterator.getNextK();
        Object val = gridIterator.next();
        
        // swap val for null string if necessary
        val = (val == null) ? "null" : val;

        double xReg = axes.tickToCoordinate(i, X);
        double yReg = axes.tickToCoordinate(j, Y);
        double zOrig = axes.tickToCoordinate(k, Z);

        double xOrig = rotationService.xRegularToOriginal(xReg, yReg, rotation);
        double yOrig = rotationService.yRegularToOriginal(xReg, yReg, rotation);

        writeLine(bufferedWriter, lineMapper.toLine(xOrig, yOrig, zOrig, val));
      }
      bufferedWriter.close();
    } catch (IOException e) {
      log.printErrorMessage("Error writing file: '" + e.getMessage() + "'.");
    }
  }

  private void writeLine(BufferedWriter bufferedWriter, String... fields)
      throws IOException {
    bufferedWriter.write(fields[0].toString());
    for (int i = 1; i < fields.length; i++) {
      bufferedWriter.write(DELIM);
      bufferedWriter.write(fields[i].toString());
    }
    bufferedWriter.write(System.lineSeparator());
  }

}
