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
 * SpreadSheetRowPoint.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.core.Utils;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;
import adams.data.spreadsheet.Cell;

/**
 * A 2-dimensional point (X: attribute index, Y: internal value).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6737 $
 */
public class SpreadSheetRowPoint
  extends AbstractDataPoint {

  /** for serialization. */
  private static final long serialVersionUID = -8737532674253304044L;

  /** the X value. */
  protected Integer m_X;

  /** the Y value. */
  protected Double m_Y;

  /**
   * Initializes the point with no points and no ID.
   */
  public SpreadSheetRowPoint() {
    this(null, null);
  }

  /**
   * Initializes the point with no ID.
   *
   * @param x		the X value
   * @param y		the Y value
   */
  public SpreadSheetRowPoint(Integer x, Double y) {
    super();

    setX(x);
    setY(y);
  }

  /**
   * Sets the X value.
   *
   * @param value	the new X value
   */
  public void setX(Integer value) {
    m_X = value;
  }

  /**
   * Returns the X value.
   *
   * @return		the X value
   */
  public Integer getX() {
    return m_X;
  }

  /**
   * Sets the Y value.
   *
   * @param value	the new Y value
   */
  public void setY(Double value) {
    m_Y = value;
  }

  /**
   * Returns the Y value.
   *
   * @return		the Y value
   */
  public Double getY() {
    return m_Y;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareTo(Object o) {
    int			result;
    SpreadSheetRowPoint other;

    if (o == null)
      return 1;
    else
      result = 0;

    other = (SpreadSheetRowPoint) o;

    if (result == 0)
      result = getX().compareTo(other.getX());

    if (result == 0)
      result = getY().compareTo(other.getY());

    return result;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataPoint other) {
    SpreadSheetRowPoint point;

    super.assign(other);

    point = (SpreadSheetRowPoint) other;

    setX(point.getX());
    setY(point.getY());
  }

  /**
   * Parses a string and instantiates a sequence point of it.
   *
   * @param s		the string to parse
   * @return		the instantiated point, null in case of an error
   */
  public SpreadSheetRowPoint parse(String s) {
    SpreadSheetRowPoint result;
    String[]		parts;

    result = null;

    parts = s.split(",");
    if (parts.length == 3)
      result = new SpreadSheetRowPoint(
	  		Integer.parseInt(parts[1]),
	  		Utils.toDouble(parts[2]));

    return result;
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;
    Cell 	cell;

    if (getParent() != null) {
      cell = ((SpreadSheetRow) getParent()).getDatasetHeader().getHeaderRow().getCell(getX());
      result  = cell.getContent();
      result += "=";
      result += getY();
    }
    else {
      result = getX() + "," + getY();
    }

    return result;
  }
}
