package grid.operator;

import grid.mask.MaskedGridIterator;

public abstract class CellOperator<INPUT_TYPE, OUTPUT_TYPE, ARG_TYPE> {
  
  private final Class<OUTPUT_TYPE> returnType;

  public CellOperator (Class<OUTPUT_TYPE> returnType) {
    this.returnType = returnType;
  }

  public Class<OUTPUT_TYPE> getReturnCellType() {
    return returnType;
  }
  
  public abstract OUTPUT_TYPE execute(MaskedGridIterator<INPUT_TYPE> maskedGridIterator, ARG_TYPE args);

}
