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
 * ProcessColumn.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instances.instancestable;

import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

/**
 * Interface for plugins that process a column.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ProcessColumn
  extends InstancesTablePopupMenuItem {

  /**
   * Processes the specified column.
   *
   * @param table	the source table
   * @param data	the instances to use as basis
   * @param column	the column in the instances
   * @return		true if successful
   */
  public boolean processColumn(InstancesTable table, Instances data, int column);
}
