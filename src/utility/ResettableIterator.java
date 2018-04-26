package utility;

import java.util.Iterator;

public interface ResettableIterator<T> extends Iterator<T> {
  
  public void reset();

}
