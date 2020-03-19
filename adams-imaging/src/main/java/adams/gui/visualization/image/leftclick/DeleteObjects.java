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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.leftclick;

import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
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
  extends AbstractLeftClickProcessor {

  private static final long serialVersionUID = -5747047661002140048L;

  /** the prefix for the objects. */
  protected String m_Prefix;

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
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  protected String getDefaultPrefix() {
    return "Object.";
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

    if (hits.size() > 0) {
      sheet = null;
      for (LocatedObject hit: hits) {
	sheetHit = hit.toSpreadSheet();
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
