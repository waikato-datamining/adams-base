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
 * Separator.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.data.report.AbstractField;
import adams.gui.visualization.report.ReportFactory;

/**
 * Dummy action that represents a separator
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Separator
  extends AbstractTableAction {

  /** for serialization. */
  private static final long serialVersionUID = -8926002876902501761L;

  /**
   * Initializes the action.
   */
  public Separator() {
    super(SEPARATOR);
  }
  
  /**
   * Does nothing.
   */
  public void actionPerformed(ActionEvent e) {
  }

  /**
   * Checks whether the action is applicable and should be added to the popup
   * menu.
   * 
   * @param table	the table the popup menu is for
   * @param row		the currently selected row
   * @param field	the field in the specified row
   * @param value	the current value
   * @return		always true
   */
  public boolean isApplicable(ReportFactory.Table table, int row, AbstractField field, String value) {
    return true;
  }
}
