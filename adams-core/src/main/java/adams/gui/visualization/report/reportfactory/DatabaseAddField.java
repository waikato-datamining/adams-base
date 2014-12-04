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
 * DatabaseAddField.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.core.option.AbstractOption;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.db.ReportProvider;
import adams.gui.core.GUIHelper;

/**
 * Allows the user to add a new field to the report. The updated report gets 
 * saved to the database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseAddField
  extends AbstractTableActionWithDatabaseAccess {

  /** for serialization. */
  private static final long serialVersionUID = 2839250960387657274L;

  /**
   * Default constructor.
   */
  public DatabaseAddField() {
    super("Add new field...");
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ReportProvider 	provider;
    Report 		report;
    String 		name;
    String 		type;
    String 		value;
    Field 		field;
    
    name = GUIHelper.showInputDialog(
	getTable(),
	"Enter the name of the field");
    if ((name == null) || (name.length() == 0))
      return;
    type = GUIHelper.showInputDialog(
	getTable(),
	"Enter the type of the field (N=numeric, B=boolean, S=string, U=unknown)", "S");
    if ((type == null) || (type.length() != 1))
      return;
    value = GUIHelper.showInputDialog(
	getTable(),
	"Enter the initial value for the field");
    if (value == null)
      return;
    field = new Field(name, DataType.valueOf((AbstractOption) null, type));
    provider = getReportProvider();
    report = getReport();
    report.setValue(field, value);
    provider.store(report.getDatabaseID(), report);
    setReport(report);
  }
}
