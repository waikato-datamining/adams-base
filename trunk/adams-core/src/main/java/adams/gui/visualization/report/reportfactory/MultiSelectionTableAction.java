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
 * MultiSelectionTableAction.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import adams.data.report.AbstractField;
import adams.gui.visualization.report.ReportFactory;

/**
 * Interface for actions that might support multi-selection.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MultiSelectionTableAction {
  
  /**
   * Checks whether the action is applicable and should be added to the popup
   * menu.
   * 
   * @param table	the table the popup menu is for
   * @param rows	the currently selected rows
   * @param fields	the fields in the specified rows
   * @param values	the current values
   * @return		true if the action is applicable, i.e., should be 
   * 			included in the popup menu
   */
  public abstract boolean isApplicable(ReportFactory.Table table, int[] rows, AbstractField[] fields, String[] values);
}
