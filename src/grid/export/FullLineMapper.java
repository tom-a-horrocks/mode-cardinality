package grid.export;

public class FullLineMapper implements LineMapper {

  @Override
  public String nonDataHeader() {
    return "X,Y,Z";
  }

  @Override
  public String[] toLine(double x, double y, double z, Object gridValue) {
    String xStr = Double.toString(x);
    String yStr = Double.toString(y);
    String zStr = Double.toString(z);
    return new String[] { xStr, yStr, zStr, gridValue.toString() };
  }

}
