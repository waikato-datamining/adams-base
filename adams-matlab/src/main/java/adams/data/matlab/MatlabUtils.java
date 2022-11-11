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
 * MatlabUtils.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.matlab;

import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.Char;
import us.hebi.matlab.mat.types.Matrix;

/**
 * Helper class for Matlab data structures.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MatlabUtils {

  /**
   * Converts a character cell into a string. Rows are interpreted as lines.
   *
   * @param matChar	the cell to convert
   * @return		the generated string
   */
  public static String charToString(Char matChar) {
    StringBuilder 	result;
    int			cols;
    int			rows;
    int			x;
    int			y;

    result = new StringBuilder();

    rows = matChar.getNumRows();
    cols = matChar.getNumCols();

    for (y = 0; y < rows; y++) {
      if (y > 0)
	result.append("\n");
      for (x = 0; x < cols; x++)
	result.append(matChar.getChar(y, x));
    }

    return result.toString();
  }

  /**
   * Increments the index.
   *
   * @param index	the current index
   * @param dims 	the dimensions (ie max values)
   * @return		true if finished
   */
  public static boolean increment(int[] index, int[] dims) {
    int		pos;

    pos = index.length - 1;
    index[pos]++;
    while (index[pos] >= dims[pos]) {
      if (pos == 0)
	return true;
      index[pos] = 0;
      pos--;
      index[pos]++;
    }

    return false;
  }

  /**
   * For transferring the subset from the original matrix into the new one.
   *
   * @param source	the source matrix
   * @param dimsSource	the dimensions of the source matrix
   * @param openSource	the indices of the "open" dimensions
   * @param indexSource the indices in the source to use
   * @param target	the target matrix
   * @param dimsTarget	the dimensions of the target matrix
   * @param type	the element type to use for transferring the data
   */
  public static void transfer(Matrix source, int[] dimsSource, int[] openSource, int[] indexSource, Matrix target, int[] dimsTarget, ArrayElementType type) {
    int		i;
    int[] 	indexTarget;
    boolean	finished;

    finished    = false;
    indexTarget = new int[dimsTarget.length];

    while (!finished) {
      for (i = 0; i < indexTarget.length; i++)
	indexSource[openSource[i]] = indexTarget[i];

      switch (type) {
	case BOOLEAN:
	  target.setBoolean(indexTarget, source.getBoolean(indexSource));
	  break;
	case BYTE:
	  target.setByte(indexTarget, source.getByte(indexSource));
	  break;
	case SHORT:
	  target.setShort(indexTarget, source.getShort(indexSource));
	  break;
	case INTEGER:
	  target.setInt(indexTarget, source.getInt(indexSource));
	  break;
	case LONG:
	  target.setLong(indexTarget, source.getLong(indexSource));
	  break;
	case FLOAT:
	  target.setFloat(indexTarget, source.getFloat(indexSource));
	  break;
	case DOUBLE:
	  target.setDouble(indexTarget, source.getDouble(indexSource));
	  break;
      }

      finished = increment(indexTarget, dimsTarget);
    }
  }

  /**
   * Returns the element according to the specified type.
   *
   * @param source	the matrix to get the element from
   * @param index	the index of the element
   * @param type	the type of element
   * @return		the value
   */
  public static Object getElement(Matrix source, int[] index, ArrayElementType type) {
    switch (type) {
      case BOOLEAN:
	return source.getBoolean(index);
      case BYTE:
	return source.getByte(index);
      case SHORT:
	return source.getShort(index);
      case INTEGER:
	return source.getInt(index);
      case LONG:
	return source.getLong(index);
      case FLOAT:
	return source.getFloat(index);
      case DOUBLE:
	return source.getDouble(index);
      default:
        throw new IllegalStateException("Unhandled element type: " + type);
    }
  }

  /**
   * Sets the element according to the specified type.
   *
   * @param source	the matrix to set the element in
   * @param index	the index of the element
   * @param type	the type of element
   * @param value 	the value of the element, gets automatically parsed if string
   * @return		null if successfully set, otherwise error message
   */
  public static String setElement(Matrix source, int[] index, ArrayElementType type, Object value) {
    String	result;

    result = null;
    try {
      // parse?
      if (value instanceof String) {
	switch (type) {
	  case BOOLEAN:
	    value = Boolean.parseBoolean((String) value);
	    break;
	  case BYTE:
	    value = Byte.parseByte((String) value);
	    break;
	  case SHORT:
	    value = Short.parseShort((String) value);
	    break;
	  case INTEGER:
	    value = Integer.parseInt((String) value);
	    break;
	  case LONG:
	    value = Long.parseLong((String) value);
	    break;
	  case FLOAT:
	    value = Float.parseFloat((String) value);
	    break;
	  case DOUBLE:
	    value = Double.parseDouble((String) value);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled element type: " + type);
	}
      }

      switch (type) {
	case BOOLEAN:
	  source.setBoolean(index, (Boolean) value);
	  break;
	case BYTE:
	  source.setByte(index, (Byte) value);
	  break;
	case SHORT:
	  source.setShort(index, (Short) value);
	  break;
	case INTEGER:
	  source.setInt(index, (Integer) value);
	  break;
	case LONG:
	  source.setLong(index, (Long) value);
	  break;
	case FLOAT:
	  source.setFloat(index, (Float) value);
	  break;
	case DOUBLE:
	  source.setDouble(index, (Double) value);
	  break;
	default:
	  throw new IllegalStateException("Unhandled element type: " + type);
      }
    }
    catch (Exception e) {
      result = "Failed to set ";
    }

    return result;
  }

  /**
   * Generates a string representation of the array dimensions.
   *
   * @param array	the array to generate the dimensions for
   * @return		the generated string
   */
  public static String arrayDimensionsToString(Array array) {
    StringBuilder 	result;

    result = new StringBuilder();
    for (int dim: array.getDimensions()) {
      if (result.length() > 0)
	result.append("x");
      result.append(dim);
    }

    return result.toString();
  }
}
