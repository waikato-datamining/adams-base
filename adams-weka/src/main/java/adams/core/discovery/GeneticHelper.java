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

/**
 * GeneticHelper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import weka.core.matrix.Matrix;

/**
 * Helper class for bit-string related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeneticHelper {

  /**
   * Turns the bit string back into an integer.
   *
   * @param bits	the bit string (0s and 1s)
   * @param min		the minimum
   * @param max		the maximum
   * @return		the integer
   */
  public static int bitsToInt(String bits, int min, int max){
    double j = 0;
    for (int i = 0; i < bits.length(); i++) {
      if (bits.charAt(i) == '1') {
        j = j + Math.pow(2, bits.length() - i - 1);
      }
    }
    j += min;

    return Math.min((int) j, max);
  }

  /**
   * Turns an integer into a bitstring (0s and 1s).
   *
   * @param in		the integer to convert
   * @param min		the minimum
   * @param max		the maximum
   * @param numBits	the number of bits
   * @return		the bit string
   */
  public static String intToBits(int in, int min, int max, int numBits){
    in = in-min;
    in = Math.min(in, max - min);
    String bits = Integer.toBinaryString(in);
    while (bits.length() < numBits)
      bits="0"+bits;
    return bits;
  }

  /**
   * Turns a bit string (0s and 1s) into an integer array.
   *
   * @param bits	the string
   * @param min		the minimum
   * @param max		the maximum
   * @param numBits	the number of bits
   * @param size	the size of the array
   * @return		the reconstructed integer array
   */
  public static int[] bitsToIntArray(String bits, int min, int max, int numBits, int size){
    int ret[] = new int[size];
    for (int k = 0; k < size; k++) {
      int start = numBits * k;
      double j = 0;
      for (int i = start; i < start + numBits; i++) {
        if (bits.charAt(i) == '1') {
          j = j + Math.pow(2, start + numBits - i - 1);
        }
      }
      j += min;
      ret[k] = (Math.min((int)j, max));
    }
    return ret;
  }

  /**
   * Creates a bit string (0s and 1s) from an integer array.
   *
   * @param ina		the integer array
   * @param min		the minimum
   * @param max		the maximum
   * @param numBits	the number of bits
   * @return		the bit string
   */
  public static String intArrayToBits(int[] ina, int min, int max, int numBits){
    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < ina.length; i++) {
      int in = ina[i];
      in = in - min;
      in = Math.min(in, max - min);
      String bits = Integer.toBinaryString(in);
      while (bits.length() < numBits)
        bits = "0" + bits;
      buff.append(bits);
    }
    return buff.toString();
  }

  /**
   * Convert weka Matrix into bit string
   * @param ina
   * @param min
   * @param max
   * @param numBits
   * @param splits
   * @param rows
   * @param columns
   * @return
   */
  public static String matrixToBits(Matrix ina, double min, double max, int numBits, int splits, int rows, int columns){
    StringBuilder buff = new StringBuilder();

    for (int row=0;row<ina.getRowDimension();row++){
      for (int column=0;column<ina.getColumnDimension();column++){
        double val=ina.get(row,column);
        buff.append(doubleToBits(val,min,max,numBits,splits));
      }
    }
    return buff.toString();
  }

  /**
   * Convert bit string into weka Matrix
   * @param bits
   * @param min
   * @param max
   * @param numBits
   * @param splits
   * @param rows
   * @param columns
   * @return
   */
  public static Matrix bitsToMatrix(String bits, double min, double max, int numBits, int splits, int rows, int columns){

    Matrix m=new Matrix(rows,columns);

    for (int row=0;row<rows;row++){
      for (int column=0;column<columns;column++){
        int start = (row*columns*numBits)+(column*numBits);
        double j=0;
        for (int i = start; i < start + numBits; i++) {
          if (bits.charAt(i) == '1') {
            j = j + Math.pow(2, start + numBits - i - 1);
          }
        }
        j = Math.min(j, splits);
        double val=(min + j*((max - min)/(double)(splits - 1)));
        m.set(row,column,val);
      }
    }

    return m;
  }


  /**
   * Turns a bit string (0s and 1s) into a double.
   *
   * @param bits	the bit string
   * @param min		the minimum
   * @param max		the maximum
   * @param splits	the number of splits
   * @return		the reconstructed double value
   */
  public static double bitsToDouble(String bits, double min, double max, int splits){
    double j = 0;
    for (int i = 0; i < bits.length(); i++) {
      if (bits.charAt(i) == '1') {
	j = j + Math.pow(2, bits.length() - i - 1);
      }
    }
    j = Math.min(j, splits);
    return (min + j*((max - min)/(double)(splits - 1)));
  }

  /**
   * Turns a double into a bit string (0s and 1s).
   *
   * @param in		the double value
   * @param min		the minimum
   * @param max		the maximum
   * @param numBits	the number of bits
   * @param splits	the number of splits
   * @return		the generated bit string
   */
  public static String doubleToBits(double in, double min, double max, int numBits, int splits){
    double sdist = (max - min) / ((double) splits - 1);
    double dist = in - min;
    double rat = dist / sdist;
    int split = (int) Math.round(rat);

    String bits = Integer.toBinaryString(split);
    while (bits.length() < numBits)
      bits="0"+bits;

    return bits;
  }
}
