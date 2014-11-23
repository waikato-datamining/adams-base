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
 * DataChart.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;
import adams.gui.tools.spreadsheetviewer.chart.AbstractChartGenerator;
import adams.gui.tools.spreadsheetviewer.chart.ScatterPlot;

/**
 * Generates a chart from the spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataChart
  extends AbstractSpreadSheetViewerMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Chart...";
  }

  /**
   * Creates a new dialog.
   * 
   * @return		the dialog
   */
  @Override
  protected GenericObjectEditorDialog createDialog() {
    GenericObjectEditorDialog	result;
    
    if (getParentDialog() != null)
      result = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      result = new GenericObjectEditorDialog(getParentFrame(), true);
    result.setTitle("Chart");
    result.getGOEEditor().setClassType(AbstractChartGenerator.class);
    result.getGOEEditor().setCanChangeClassInDialog(true);
    result.getGOEEditor().setValue(new ScatterPlot());
    result.setLocationRelativeTo(m_State);
    
    return result;
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SpreadSheetPanel		panel;
    AbstractChartGenerator	generator;

    panel = getTabbedPane().getCurrentPanel();
    if (panel == null)
      return;

    getDialog().setVisible(true);
    if (getDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    generator = (AbstractChartGenerator) getDialog().getGOEEditor().getValue();
    panel.generateChart(generator);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(isSheetSelected());
  }
}
