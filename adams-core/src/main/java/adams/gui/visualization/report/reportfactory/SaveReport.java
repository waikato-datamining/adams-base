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
 * SaveReport.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report.reportfactory;

import java.awt.event.ActionEvent;

import adams.data.io.output.AbstractReportWriter;
import adams.data.report.AbstractField;
import adams.gui.chooser.AbstractReportFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory;

/**
 * Saves the report.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SaveReport
  extends AbstractTableAction {

  /** for serialization. */
  private static final long serialVersionUID = -131693262283412499L;

  /**
   * Default constructor.
   */
  public SaveReport() {
    super("Save report...");
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
  public void actionPerformed(ActionEvent e) {
    int		 		retVal;
    AbstractReportWriter 	writer;
    
    if (GUIHelper.getParentDialog(getTable()) != null)
      retVal = getFileChooser().showSaveDialog(GUIHelper.getParentDialog(getTable()));
    else
      retVal = getFileChooser().showSaveDialog(GUIHelper.getParentFrame(getTable()));
    if (retVal != AbstractReportFileChooser.APPROVE_OPTION)
      return;
    writer = (AbstractReportWriter) getFileChooser().getWriter();
    writer.write(getReport());
    writer.cleanUp();
  }
}
