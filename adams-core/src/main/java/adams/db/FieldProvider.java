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
 * FieldProvider.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.Vector;

import adams.data.report.AbstractField;
import adams.data.report.DataType;

/**
 * Interface for table classes that return Field objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AbstractIndexedTable
 */
public interface FieldProvider<T extends AbstractField>
  extends DatabaseConnectionProvider {

  /**
   * Returns all available fields.
   *
   * @return		the list of fields
   */
  public Vector<T> getFields();

  /**
   * Returns all available fields.
   *
   * @param dtype	the type to limit the search to, use "null" for all
   * @return		the list of fields
   */
  public Vector<T> getFields(DataType dtype);
}
