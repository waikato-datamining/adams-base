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
 * DatabaseRemoveField.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.db.ReportProvider;
import adams.gui.visualization.report.ReportFactory;

/**
 * Removes the field from the report and updates the report in the database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseRemoveField
  extends AbstractTableActionWithDatabaseAccess 
  implements MultiSelectionTableAction {

  /** for serialization. */
  private static final long serialVersionUID = -7727702763234836816L;

  /**
   * Default constructor.
   */
  public DatabaseRemoveField() {
    super("Remove field");
  }

  /**
   * Sets the fields to use.
   * 
   * @param value	the fields
   */
  protected void setFields(AbstractField[] value) {
    super.setFields(value);
    setName(createName("Remove field", value, 50));
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
    boolean	result;
    int		i;
    
    result = true;
    for (i = 0; i < rows.length; i++)
      result = result && isApplicable(table, rows[i], fields[i], values[i]);
    
    return result;
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  public void actionPerformed(ActionEvent e) {
    ReportProvider 	provider;
    Report 		report;
    
    provider = getReportProvider();
    report   = getReport();
    for (AbstractField field: getFields())
      report.removeValue(field);
    provider.store(report.getDatabaseID(), report);
    setReport(report);
  }
}
