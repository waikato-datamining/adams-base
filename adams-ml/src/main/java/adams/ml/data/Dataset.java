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
 * Dataset.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.data;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Extended {@link SpreadSheet} class, providing additional machine
 * learning functionality.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Dataset
  extends SpreadSheet {

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  @Override
  public Dataset getClone();

  /**
   * Returns the a spreadsheet with the same header and comments.
   *
   * @return		the spreadsheet
   */
  public Dataset getHeader();

  /**
   * Returns the index of the column using the specified name.
   *
   * @param name	the name of the column to locate
   * @return		the index, -1 if failed to locate
   */
  public int indexOfColumn(String name);

  /**
   * Removes all set class attributes.
   */
  public void removeClassAttributes();

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colKey	they key of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(String colKey);

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param name	they name of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttributeByName(String name);

  /**
   * Returns whether the specified column is a class attribute.
   *
   * @param colIndex	they index of the column to query
   * @return		true if column a class attribute
   */
  public boolean isClassAttribute(int colIndex);

  /**
   * Sets the class attribute status for a column.
   *
   * @param colKey	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(String colKey, boolean isClass);

  /**
   * Sets the class attribute status for a column.
   *
   * @param name	the name of the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttributeByName(String name, boolean isClass);

  /**
   * Sets the class attribute status for a column.
   *
   * @param colIndex	the column to set the class attribute status for
   * @param isClass	if true then the column will be flagged as class
   * 			attribute, otherwise the flag will get removed
   * @return		true if successfully updated
   */
  public boolean setClassAttribute(int colIndex, boolean isClass);

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column keys of class attributes (not ordered)
   */
  public String[] getClassAttributeKeys();

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the column names of class attributes (not ordered)
   */
  public String[] getClassAttributeNames();

  /**
   * Returns all the class attributes that are currently set.
   *
   * @return		the indices of class attributes (sorted asc)
   */
  public int[] getClassAttributeIndices();

  /**
   * Returns a spreadsheet containing only the input columns, not class
   * columns.
   *
   * @return		the input features, null if data conists only of class columns
   */
  public SpreadSheet getInputs();

  /**
   * Returns a spreadsheet containing only output columns, i.e., the class
   * columns.
   *
   * @return		the output features, null if data has no class columns
   */
  public SpreadSheet getOutputs();
}
