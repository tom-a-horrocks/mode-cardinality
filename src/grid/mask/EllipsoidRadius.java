package grid.mask;

public class EllipsoidRadius {

  private final double xRadius;
  private final double yRadius;
  private final double zRadius;

  public EllipsoidRadius(double localRadius) {
    this.xRadius = localRadius;
    this.yRadius = localRadius;
    this.zRadius = localRadius;
  }

  public EllipsoidRadius(double[] axesRadii) {
    this.xRadius = axesRadii[0];
    this.yRadius = axesRadii[1];
    this.zRadius = axesRadii[2];
  }

  public double x() {
    return xRadius;
  }

  public double y() {
    return yRadius;
  }

  public double z() {
    return zRadius;
  }
  
  public double xSquared() {
    return xRadius * xRadius;
  }

  public double ySquared() {
    return yRadius * yRadius;
  }

  public double zSquared() {
    return zRadius * zRadius;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(xRadius);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(yRadius);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(zRadius);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EllipsoidRadius other = (EllipsoidRadius) obj;
    if (Double.doubleToLongBits(xRadius) != Double
        .doubleToLongBits(other.xRadius))
      return false;
    if (Double.doubleToLongBits(yRadius) != Double
        .doubleToLongBits(other.yRadius))
      return false;
    if (Double.doubleToLongBits(zRadius) != Double
        .doubleToLongBits(other.zRadius))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EllipsoidRadius [xRadius=" + xRadius + ", yRadius=" + yRadius
        + ", zRadius=" + zRadius + "]";
  }
  
  
}
