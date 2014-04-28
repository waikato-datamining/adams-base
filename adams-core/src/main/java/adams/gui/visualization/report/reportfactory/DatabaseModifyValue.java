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
 * DatabaseModifyValue.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Report;
import adams.db.ReportProvider;
import adams.gui.visualization.report.ReportFactory;

/**
 * Allows the user to modify values. The underlying report gets updated in the
 * database in case of a change.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseModifyValue
  extends AbstractTableActionWithDatabaseAccess {

  /** for serialization. */
  private static final long serialVersionUID = -8462082833012930410L;

  /**
   * Default constructor.
   */
  public DatabaseModifyValue() {
    super("Modify");
  }

  /**
   * Sets the fields to use.
   * 
   * @param value	the fields
   */
  protected void setFields(AbstractField[] value) {
    super.setFields(value);
    
    if ((getFields().length == 0) && (getFields()[0].getDataType() == DataType.BOOLEAN))
      setName("Toggle '" + getFields()[0].toDisplayString() + "'");
    else
      setName(createName("Modify '", value, 50));
  }
  
  /**
   * Checks whether the action is applicable and should be added to the popup
   * menu.
   * 
   * @param table	the table the popup menu is for
   * @param row		the currently selected row
   * @param field	the field in the specified row
   * @param value	the current value
   * @return		true if the action is applicable, i.e., should be 
   * 			included in the popup menu
   */
  public boolean isApplicable(ReportFactory.Table table, int row, AbstractField field, String value) {
    boolean	result;
    
    result = super.isApplicable(table, row, field, value);

    if (result) {
      switch (field.getDataType()) {
	case BOOLEAN:
	  result = (parseBoolean(value) != null);
	  break;
	case NUMERIC:
	  result = (parseDouble(value) != null);
      }
    }
    
    return result;
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  public void actionPerformed(ActionEvent e) {
    String		value;
    ReportProvider 	provider;
    Report 		report;
    boolean		modified;
    int			i;
    AbstractField	field;
    
    provider = getReportProvider();
    report   = getReport();
    modified = false;
    
    for (i = 0; i < getFields().length; i++) {
      field = getFields()[i];
      value = getValues()[i];
      switch (field.getDataType()) {
	case BOOLEAN:
	  report.setValue(field, !parseBoolean(value));
	  modified = true;
	  break;

	case NUMERIC:
	  Double valueDbl = parseDouble(value);
	  if (valueDbl == null)
	    valueDbl = 0.0;
	  String valueInit = valueDbl.toString();
	  do {
	    String newValue = JOptionPane.showInputDialog(
		"Enter numeric value for '" + field.toDisplayString() + "'", valueInit);
	    if (newValue == null)
	      return;
	    valueDbl = parseDouble(newValue);
	  }
	  while (valueDbl == null);
	  report.setValue(field, valueDbl);
	  modified = true;
	  break;

	case STRING:
	  String newValue = JOptionPane.showInputDialog(
	      "Enter value for '" + field.toDisplayString() + "'", value);
	  if (newValue == null)
	    return;
	  report.setValue(field, newValue);
	  modified = true;
	  break;

	default:
	  System.err.println(getClass().getName() + "/actionPerformed: unhandled data type '" + field.getDataType() + "'!");
      }
    }

    if (modified) {
      provider.store(report.getDatabaseID(), report);
      setReport(report);
    }
  }
}
