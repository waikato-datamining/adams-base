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
 * ViewObjects.java
 * Copyright (C) 2020-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.mouseclick;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;

/**
 * Displays the annotated objects in a dialog that contain the click position.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViewObjects
  extends AbstractMouseClickProcessor {

  private static final long serialVersionUID = -5747047661002140048L;

  /** the keys in the meta-data. */
  protected BaseString[] m_MetaDataKeys;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the annotated objects in a dialog that contain the click position.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "meta-data-key", "metaDataKeys",
      getDefaultMetaDataKeys());
  }

  /**
   * Returns the default meta-data keys.
   *
   * @return		the default
   */
  protected BaseString[] getDefaultMetaDataKeys() {
    return new BaseString[]{new BaseString("type")};
  }

  /**
   * Sets the meta-data keys to display in separate columns.
   *
   * @param value 	the keys
   */
  public void setMetaDataKeys(BaseString[] value) {
    m_MetaDataKeys = value;
    reset();
  }

  /**
   * Returns the meta-data keys to display in separate columns.
   *
   * @return 		the keys
   */
  public BaseString[] getMetaDataKeys() {
    return m_MetaDataKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeysTipText() {
    return "The keys in the meta-data to display as separate columns.";
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  @Override
  protected void doProcess(ObjectAnnotationPanel panel, MouseEvent e) {
    LocatedObjects	hits;
    SpreadSheet		sheet;
    SpreadSheet		sheetHit;
    SpreadSheetDialog 	dialog;

    hits = determineHits(panel, e);

    if (!hits.isEmpty()) {
      sheet = null;
      for (LocatedObject hit: hits) {
	sheetHit = hit.toSpreadSheet(BaseObject.toStringArray(m_MetaDataKeys));
	if (sheet == null)
	  sheet = sheetHit;
	else
	  sheet.addRow().assign(sheetHit.getRow(0));
      }
      if (panel.getParentDialog() != null)
	dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(panel.getParentFrame(), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Objects");
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(panel);
      dialog.setSpreadSheet(sheet);
      dialog.setShowSearch(true);
      dialog.setVisible(true);
    }
  }
}
