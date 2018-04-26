package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BufferService {

  public BufferedReader getReadBuffer(String path) throws FileNotFoundException {
    return new BufferedReader(new FileReader(path));
  }

  public BufferedWriter getWriteBuffer(String path) throws IOException {
    return new BufferedWriter(new FileWriter(path));
  }

}
