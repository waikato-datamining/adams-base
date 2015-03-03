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
 * InstanceTableColumnNameGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.gui.visualization.container.DefaultContainerTableColumnNameGenerator;

/**
 * Abstract class for generating the column names of a table.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceContainerTableColumnNameGenerator
  extends DefaultContainerTableColumnNameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -6782027333845657467L;

  /**
   * Returns the name of the column with the actual data in it.
   *
   * @return		the name
   */
  public String getData() {
    return "ID";
  }

  /**
   * Returns the width of the data column.
   *
   * @return		the width
   */
  public int getDataWidth() {
    return 120;
  }
}