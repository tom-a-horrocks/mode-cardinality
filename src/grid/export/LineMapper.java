package grid.export;

public interface LineMapper {

  public String nonDataHeader();

  public String[] toLine(double x, double y, double z, Object val);

}