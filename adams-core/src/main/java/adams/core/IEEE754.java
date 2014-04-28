/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * IEEE754.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * Helper class for dealing with ieee754 encoded floats.
 *
 * @author dale
 * @version $Revision$
 */
public class IEEE754 {

  /**
   * Convert ieee754 int to double.
   *
   * @param i	ieee754 int
   * @return	float
   */
  public static double toDouble(double i) {
    long l=(long)i;
    double ret=Float.intBitsToFloat(longToIntBits(l));
    if (Double.isNaN(ret)) {
      System.err.println("Encountered non-IEEE 754 floating-point value");
      return(Double.NaN);
    }
    return(ret);
  }

  /**
   * Convert long to int bitwise.
   *
   * @param l		long
   * @return		int
   */
  public static int longToIntBits(long l) {
    int ret=(int)(l >> 24);
    ret <<=8;
    ret |= (int)((l >> 16) & 0xff);
    ret <<=8;
    ret |= (int)((l >> 8) & 0xff);
    ret <<=8;
    ret |= (int)(l & 0xff);
    return(ret);
  }

  /**
   * Convert double to ieee754 as long.
   *
   * @param d	double
   * @return	ieee754
   */
  public static long toIntBits(double d) {
    float f=(float)d;
    int i=Float.floatToRawIntBits(f);
    long l=0;
    return(l | i);
  }

  public static byte[] floatToIntBitsLittleEndian(Float f){
    byte[] ret=new byte[4];
    int i=Float.floatToRawIntBits(f);
    ret[0] |= ((i & (0xff000000)) >> 24);
    ret[1] |= ((i & (0x0ff0000)) >> 16);
    ret[2] |= ((i & (0x00ff00)) >> 8);
    ret[3] |= (i & (0x000ff));
    return(ret);
  }

  /**
   * Convert ieee754 array(as doubles) to double array.
   *
   * @param in	ieee754 array(as doubles)
   * @return	double array
   */
  public static double[] toDoubleArray(double[] in) {
    double ret[] = new double[in.length];
    for (int i=0;i<in.length;i++) {
      ret[i]=IEEE754.toDouble(in[i]);
    }
    return(ret);
  }

  /**
   * Convert double array to ieee754 array(as doubles) .
   *
   * @param in	double array
   * @return	double array
   */
  public static double[] toIntBitsArray(double[] in) {
    double ret[] = new double[in.length];
    for (int i=0;i<in.length;i++) {
      ret[i]=(double)IEEE754.toIntBits(in[i]);
    }
    return(ret);
  }
}
