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
 * RemoveField.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.report.ReportFactory.Table;

/**
 * Removes the field from the report.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveField
  extends AbstractTableAction 
  implements MultiSelectionTableAction {

  /** for serialization. */
  private static final long serialVersionUID = -7727702763234836816L;

  /**
   * Default constructor.
   */
  public RemoveField() {
    super("Remove field");
  }

  /**
   * Sets the fields to use.
   * 
   * @param value	the fields
   */
  @Override
  protected void setFields(AbstractField[] value) {
    super.setFields(value);
    setName(createName("Remove field", value, 50));
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Report 	report;
    
    report = getReport();
    for (AbstractField field: getFields())
      report.removeValue(field);
    setReport(report);
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
  @Override
  public boolean isApplicable(Table table, int row, AbstractField field, String value) {
    return true;
  }
  
  /**
   * Checks whether the action is applicable and should be added to the popup
   * menu.
   * 
   * @param table	the table the popup menu is for
   * @param rows	the currently selected rows
   * @param fields	the fields in the specified row
   * @param values	the current values
   * @return		true if the action is applicable, i.e., should be 
   * 			included in the popup menu
   */
  public boolean isApplicable(ReportFactory.Table table, int[] rows, AbstractField[] fields, String[] values) {
    return true;
  }
}
