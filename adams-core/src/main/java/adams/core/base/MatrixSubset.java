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
 * MatrixSubset.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.base;

import adams.core.Range;
import adams.core.Utils;

/**
 * For defining matrix subsets '<rows>,<cols>'. Leaving a part empty means
 * using all of them, eg ',3:4' returns all rows but only columns 3 and 4.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatrixSubset
  extends AbstractBaseString {

  private static final long serialVersionUID = -6303299773472435674L;

  /** the rows. */
  protected Range m_Rows;

  /** the columns. */
  protected Range m_Columns;

  /**
   * Initializes the subset with all rows/cols.
   */
  public MatrixSubset() {
    super("");
  }

  /**
   * Initializes the subset with the specified subset.
   *
   * @param subset	the subset to use
   */
  public MatrixSubset(String subset) {
    super(subset);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Rows    = new Range(Range.ALL);
    m_Columns = new Range(Range.ALL);
  }

  /**
   * Checks whether a row/column specification is valid.
   *
   * @param part	the part to check
   * @return		the range, null if invalid format
   */
  protected Range parseRange(String part) {
    String[]	parts;
    int		from;
    int		to;

    if (part.isEmpty())
      return new Range(Range.ALL);

    parts = part.split(":");
    if (parts.length != 2)
      return null;

    if (!Utils.isInteger(parts[0]) || !Utils.isInteger(parts[1]))
      return null;

    from = Integer.parseInt(parts[0]);
    to   = Integer.parseInt(parts[1]);

    if ((from > 0) && (to > 0) && (to >= from))
      return new Range(from + "-" + to);

    return null;
  }

  /**
   * Splits the subset expression.
   *
   * @param value	the expression
   * @return		the parts, 0-length if failed to split
   */
  protected String[] split(String value) {
    String[] 	result;

    if (!value.contains(","))
      return new String[0];

    result    = new String[2];
    result[0] = value.substring(0, value.indexOf(","));
    result[1] = value.substring(result[0].length() + 1, value.length());

    return result;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    String[]	parts;

    if (value.isEmpty())
      return true;

    parts = split(value);
    if (parts.length != 2)
      return false;

    return (parseRange(parts[0]) != null) && (parseRange(parts[1]) != null);
  }

  /**
   * Converts the string according to the specified conversion.
   *
   * @param value	the string to convert
   * @return		the converted string
   */
  protected String convert(String value) {
    String[]	parts;

    if (value.isEmpty() || value.equals(",")) {
      m_Rows    = new Range(Range.ALL);
      m_Columns = new Range(Range.ALL);
    }
    else {
      parts     = split(value);
      m_Rows    = parseRange(parts[0]);
      m_Columns = parseRange(parts[1]);
    }

    return value;
  }

  /**
   * Returns the rows range.
   *
   * @return		the range
   */
  public Range rowsValue() {
    return m_Rows;
  }

  /**
   * Returns the columns range.
   *
   * @return		the range
   */
  public Range columnsValue() {
    return m_Columns;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "R-like matrix subsets with format '<rows>,<cols>'; eg '1:4,' means rows 1 to 4 and all columns.";
  }
}
