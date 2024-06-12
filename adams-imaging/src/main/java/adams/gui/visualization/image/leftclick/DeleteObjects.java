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
 * DeleteObjects.java
 * Copyright (C) 2020-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.leftclick;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Dialog.ModalityType;
import java.awt.Point;

/**
 * Displays the objects at the click position and deletes the selected ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeleteObjects
  extends AbstractLeftClickProcessor
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -5747047661002140048L;

  /** the prefix for the objects. */
  protected String m_Prefix;

  /** the keys in the meta-data. */
  protected BaseString[] m_MetaDataKeys;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the objects at the click position and deletes the selected ones.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      getDefaultPrefix());

    m_OptionManager.add(
      "meta-data-key", "metaDataKeys",
      getDefaultMetaDataKeys());
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  protected String getDefaultPrefix() {
    return LocatedObjects.DEFAULT_PREFIX;
  }

  /**
   * Sets the prefix to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the fields in the report of the image.";
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
   * Process the click that occurred in the image panel.
   *
   * @param panel	the origin
   * @param position	the position of the click
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessClick(ImagePanel panel, Point position, int modifiersEx) {
    LocatedObjects	objects;
    LocatedObjects	hits;
    Point		location;
    boolean		add;
    Report 		report;
    Report		reportNew;
    SpreadSheet 	sheet;
    SpreadSheet		sheetHit;
    SpreadSheetDialog 	dialog;
    int[]		rows;

    objects  = LocatedObjects.fromReport(panel.getAllProperties(), m_Prefix);
    hits     = new LocatedObjects();
    location = panel.mouseToPixelLocation(position);
    for (LocatedObject object: objects) {
      if (object.hasPolygon())
	add = object.getActualPolygon().contains(location);
      else
	add = object.getActualRectangle().contains(location);
      if (add)
	hits.add(object);
    }

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
	dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	dialog = new SpreadSheetDialog(panel.getParentFrame(), true);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Select objects to remove");
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(panel);
      dialog.setSpreadSheet(sheet);
      dialog.setShowSearch(true);
      dialog.setVisible(true);
      if (dialog.getOption() != SpreadSheetDialog.APPROVE_OPTION)
        return;

      rows = dialog.getTable().getSelectedRows();
      if (rows.length == 0)
        return;

      for (int row: rows)
	objects.remove(hits.get(row));
      report    = panel.getAdditionalProperties().getClone();
      report.removeValuesStartingWith(m_Prefix);
      reportNew = objects.toReport(m_Prefix);
      reportNew.mergeWith(report);
      panel.setAdditionalProperties(reportNew);
    }
  }
}
