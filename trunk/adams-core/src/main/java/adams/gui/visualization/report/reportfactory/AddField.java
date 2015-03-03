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
 * AddField.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.core.option.AbstractOption;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory.Table;

/**
 * Allows the user to add a new field to the report.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddField
  extends AbstractTableAction {

  /** for serialization. */
  private static final long serialVersionUID = 2839250960387657274L;

  /**
   * Default constructor.
   */
  public AddField() {
    super("Add new field...");
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    Report 	report;
    String 	name;
    String 	type;
    String 	value;
    Field 	field;
    
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
    field  = new Field(name, DataType.valueOf((AbstractOption) null, type));
    report = getReport();
    report.setValue(field, value);
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
}
