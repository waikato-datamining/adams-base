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
 * WekaGeneticHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.genetic;

import weka.core.matrix.Matrix;

/**
 * Helper for Weka classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGeneticHelper
  extends GeneticHelper {

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
}
