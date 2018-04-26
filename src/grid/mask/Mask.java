package grid.mask;

import java.util.Arrays;

public class Mask {

  private final boolean[][][] mask;
  private final int iMid;
  private final int jMid;
  private final int kMid;
  private final int iLength;
  private final int jLength;
  private final int kLength;

  public Mask(boolean[][][] mask, int iMid, int jMid, int kMid, int iLength,
      int jLength, int kLength) {
    this.mask = mask;
    this.iMid = iMid;
    this.jMid = jMid;
    this.kMid = kMid;
    this.iLength = iLength;
    this.jLength = jLength;
    this.kLength = kLength;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + iLength;
    result = prime * result + iMid;
    result = prime * result + jLength;
    result = prime * result + jMid;
    result = prime * result + kLength;
    result = prime * result + kMid;
    result = prime * result + Arrays.deepHashCode(mask);
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
    Mask other = (Mask) obj;
    if (iLength != other.iLength)
      return false;
    if (iMid != other.iMid)
      return false;
    if (jLength != other.jLength)
      return false;
    if (jMid != other.jMid)
      return false;
    if (kLength != other.kLength)
      return false;
    if (kMid != other.kMid)
      return false;
    if (!Arrays.deepEquals(mask, other.mask))
      return false;
    return true;
  }

  public int iLength() {
    return iLength;
  }

  public int jLength() {
    return jLength;
  }

  public int kLength() {
    return kLength;
  }

  public int iMid() {
    return iMid;
  }

  public int jMid() {
    return jMid;
  }

  public int kMid() {
    return kMid;
  }
  
  public boolean included(int i, int j, int k) {
    return mask[i][j][k];
  }

  private int numIncluded() {
    int numIncluded = 0;
    for (int i = 0; i < iLength; i++) {
      for (int j = 0; j < jLength; j++) {
        for (int k = 0; k < kLength; k++) {
          if (included(i, j, k)) {
            numIncluded++;
          }
        }
      }
    }
    return numIncluded;
  }

  @Override
  public String toString() {
    return "Mask [iLength=" + iLength + ", jLength=" + jLength + ", kLength="
        + kLength + ", numIncluded()=" + numIncluded() + "]";
  }
  

}
