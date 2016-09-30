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
 * ViewDataClickAction.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.stats.scatterplot.action;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotHitDetector;
import adams.gui.visualization.stats.paintlet.ScatterPlotCircleHitDetector;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;

import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;

/**
 * Displays the data that the user clicked on in a table.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewDataClickAction
  extends AbstractMouseClickAction {

  /** for serialization. */
  private static final long serialVersionUID = -1383042782074675611L;
  
  /** the hit detector to use. */
  protected AbstractScatterPlotHitDetector m_HitDetector;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the data that the user clicked on.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "hit-detector", "hitDetector",
	     new ScatterPlotCircleHitDetector());
  }

  /**
   * Sets the hit detector to use.
   *
   * @param value 	the hit detector
   */
  public void setHitDetector(AbstractScatterPlotHitDetector value) {
    m_HitDetector = value;
    reset();
  }

  /**
   * Returns the hit detector to use.
   *
   * @return 		the hit detector
   */
  public AbstractScatterPlotHitDetector getHitDetector() {
    return m_HitDetector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hitDetectorTipText() {
    return "The hit detector to use.";
  }

  /**
   * Gets called in case of a left-click.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  protected void processLeftClick(ScatterPlot panel, MouseEvent e) {
    Object			located;
    int[]			hits;
    SpreadSheet			sheet;
    SpreadSheetDialog		dialog;

    if (m_HitDetector.getOwner() != panel.getPaintlet())
      m_HitDetector.setOwner(panel.getPaintlet());
    located = m_HitDetector.locate(e);
    if (located instanceof int[]) {
      hits  = (int[]) located;
      sheet = m_HitDetector.getOwner().getData().getHeader();
      for (int hit: hits)
	sheet.addRow().assign(m_HitDetector.getOwner().getData().getRow(hit));

      if (sheet.getRowCount() > 0) {
	if (panel.getParentDialog() != null)
	  dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.MODELESS);
	else
	  dialog = new SpreadSheetDialog(panel.getParentFrame(), false);
	dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
	dialog.setTitle("Data");
	dialog.setSize(GUIHelper.getDefaultDialogDimension());
	dialog.setLocationRelativeTo(panel);
	dialog.setSpreadSheet(sheet);
	dialog.setVisible(true);
      }
    }
  }

  /**
   * Does nothing.
   * 
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  protected void processRightClick(ScatterPlot panel, MouseEvent e) {
  }
}
