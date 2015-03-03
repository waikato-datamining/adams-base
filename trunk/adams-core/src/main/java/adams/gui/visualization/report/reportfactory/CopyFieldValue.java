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
 * CopyFieldValue.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.data.report.AbstractField;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory;


/**
 * Copies the field value.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyFieldValue
  extends AbstractTableAction 
  implements MultiSelectionTableAction {

  /** for serialization. */
  private static final long serialVersionUID = 3873065739520557038L;

  /**
   * Default constructor.
   */
  public CopyFieldValue() {
    super("Copy field value");
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
  @Override
  public boolean isApplicable(ReportFactory.Table table, int row, AbstractField field, String value) {
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
    return (rows.length > 0);
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    StringBuilder	str;
    int			i;
    
    str = new StringBuilder();
    for (i = 0; i < getFields().length; i++) {
      if (i > 0)
	str.append(System.getProperty("line.separator"));
      str.append("" + getValues()[i]);
    }
    
    GUIHelper.copyToClipboard(str.toString());
  }
}
