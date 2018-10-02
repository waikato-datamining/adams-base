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
 * DatasetUtils.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import com.github.waikatodatamining.matrix.core.MatrixFactory;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for {@link Dataset} objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DatasetUtils
  extends SpreadSheetHelper {

  /**
   * Determines the numeric columns in a dataset.
   *
   * @param data	the dataset to inspect
   * @return		the indices of the numeric columns
   */
  public static int[] getNumericColumns(Dataset data) {
    TIntList 	result;
    int		i;

    result = new TIntArrayList();
    for (i = 0; i < data.getColumnCount(); i++) {
      if (data.isNumeric(i))
        result.add(i);
    }

    return result.toArray();
  }

  /**
   * Turns all the numeric columns into a Jama Matrix.
   *
   * @param data	the data to convert
   * @return		the generated matrix
   */
  public static Jama.Matrix numericToJama(Dataset data) {
    return numericToJama(data, getNumericColumns(data));
  }

  /**
   * Turns the specified numeric columns into a Jama Matrix.
   *
   * @param data	the data to convert
   * @param cols	the numeric columns to use
   * @return		the generated matrix
   */
  public static Jama.Matrix numericToJama(Dataset data, int[] cols) {
    Jama.Matrix	result;
    int		y;
    int		x;

    result = new Jama.Matrix(data.getRowCount(), cols.length);
    for (y = 0; y < data.getRowCount(); y++) {
      for (x = 0; x < cols.length; x++)
        result.set(y, x, data.getRow(y).getCell(cols[x]).toDouble());
    }

    return result;
  }

  /**
   * Turns all the numeric columns into a Jama Matrix.
   *
   * @param data	the data to convert
   * @return		the generated matrix
   */
  public static com.github.waikatodatamining.matrix.core.Matrix numericToMatrixAlgo(Dataset data) {
    return numericToMatrixAlgo(data, getNumericColumns(data));
  }

  /**
   * Turns the specified numeric columns into a Jama Matrix.
   *
   * @param data	the data to convert
   * @param cols	the numeric columns to use
   * @return		the generated matrix
   */
  public static com.github.waikatodatamining.matrix.core.Matrix numericToMatrixAlgo(Dataset data, int[] cols) {
    com.github.waikatodatamining.matrix.core.Matrix	result;
    int							y;
    int							x;

    result = MatrixFactory.zeros(data.getRowCount(), cols.length);
    for (y = 0; y < data.getRowCount(); y++) {
      for (x = 0; x < cols.length; x++)
        result.set(y, x, data.getRow(y).getCell(cols[x]).toDouble());
    }

    return result;
  }

  /**
   * Turns all the numeric columns into a list of double arrays.
   *
   * @param data	the data to convert
   * @return		the generated arrays
   */
  public static List<double[]> numericToArrays(Dataset data) {
    return numericToArrays(data, getNumericColumns(data));
  }

  /**
   * Turns the specified numeric columns into a list of double arrays.
   *
   * @param data	the data to convert
   * @param cols	the numeric columns to use
   * @return		the generated arrays
   */
  public static List<double[]> numericToArrays(Dataset data, int[] cols) {
    List<double[]>	result;
    double[]		array;

    result = new ArrayList<>();
    for (Row row: data.rows()) {
      array = new double[cols.length];
      for (int x: cols)
        array[x] = row.getCell(x).toDouble();
      result.add(array);
    }

    return result;
  }

  /**
   * Returns the matrix as spreadsheet.
   *
   * @param data	the matrix to convert
   * @param colPrefix 	the prefix for the columns
   * @return		the generated spreadsheet
   */
  public static SpreadSheet matrixAlgoToSpreadSheet(com.github.waikatodatamining.matrix.core.Matrix data, String colPrefix) {
    SpreadSheet		result;
    Row			row;
    int			x;
    int			y;

    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    for (x = 0; x < data.numColumns(); x++)
      row.addCell("" + x).setContentAsString(colPrefix + (x+1));

    for (y = 0; y < data.numRows(); y++) {
      row = result.addRow();
      for (x = 0; x < data.numColumns(); x++)
	row.addCell("" + x).setContent(data.get(y, x));
    }

    return result;
  }
}
