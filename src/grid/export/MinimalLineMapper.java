package grid.export;

public class MinimalLineMapper implements LineMapper {

  @Override
  public String nonDataHeader() {
    return "";
  }

  @Override
  public String[] toLine(double x, double y, double z, Object gridValue) {
    return new String[] { gridValue.toString() };
  }

}
