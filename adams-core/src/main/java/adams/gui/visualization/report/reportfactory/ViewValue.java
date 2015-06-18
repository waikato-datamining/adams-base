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
 * ViewValue.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import adams.data.report.AbstractField;
import adams.gui.dialog.TextDialog;
import adams.gui.visualization.report.ReportFactory;

import java.awt.event.ActionEvent;

/**
 * Allows the user to view values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewValue
  extends AbstractTableAction {

  /** for serialization. */
  private static final long serialVersionUID = -8462082833012930410L;

  /**
   * Default constructor.
   */
  public ViewValue() {
    super("View");
  }

  /**
   * Sets the fields to use.
   * 
   * @param value	the fields
   */
  @Override
  protected void setFields(AbstractField[] value) {
    super.setFields(value);
    setName(createName("View", value, 50));
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
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    int			i;
    StringBuilder	content;
    TextDialog		dialog;
    
    content = new StringBuilder();
    for (i = 0; i < getFields().length; i++) {
      if (content.length() > 0)
	content.append("\n");
      content.append(getFields()[i] + "\n");
      content.append(getValues()[i] + "\n");
    }
    dialog = new TextDialog();
    dialog.setDialogTitle("Report values");
    dialog.setContent(content.toString());
    dialog.setSize(600, 600);
    dialog.setLocationRelativeTo(getTable());
    dialog.setVisible(true);
  }
}
