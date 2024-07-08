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

/*
 * ViewDataClickAction.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CircleHitDetector;

import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Displays the data that the user clicked on in a table.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ViewDataClickAction
  extends AbstractMouseClickAction {

  /** for serialization. */
  private static final long serialVersionUID = -1383042782074675611L;
  
  /** the hit detector to use. */
  protected AbstractXYSequencePointHitDetector m_HitDetector;
  
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
	     new CircleHitDetector());
  }

  /**
   * Sets the hit detector to use.
   *
   * @param value 	the hit detector
   */
  public void setHitDetector(AbstractXYSequencePointHitDetector value) {
    m_HitDetector = value;
    reset();
  }

  /**
   * Returns the hit detector to use.
   *
   * @return 		the hit detector
   */
  public AbstractXYSequencePointHitDetector getHitDetector() {
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
  protected void processLeftClick(SequencePlotterPanel panel, MouseEvent e) {
    Object			located;
    List<XYSequencePoint> 	hits;
    SpreadSheet			sheet;
    SpreadSheetDialog		dialog;

    if (m_HitDetector.getOwner() != panel.getDataPaintlet())
      m_HitDetector.setOwner(panel.getDataPaintlet());
    located = m_HitDetector.locate(e);
    if (located instanceof List) {
      hits   = (List<XYSequencePoint>) located;
      if (hits.isEmpty())
	sheet = new DefaultSpreadSheet();
      else
	sheet = ((XYSequence) hits.get(0).getParent()).toSpreadSheet(hits);

      if (sheet.getRowCount() > 0) {
	if (panel.getParentDialog() != null)
	  dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.MODELESS);
	else
	  dialog = new SpreadSheetDialog(panel.getParentFrame(), false);
	dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
	dialog.setTitle(panel.getTitle());
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
  protected void processRightClick(SequencePlotterPanel panel, MouseEvent e) {
  }
}
