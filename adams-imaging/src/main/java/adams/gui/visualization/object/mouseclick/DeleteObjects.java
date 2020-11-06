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

package adams.gui.visualization.object.mouseclick;

import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;

/**
 * Displays the objects at the click position and deletes the selected ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeleteObjects
  extends AbstractMouseClickProcessor {

  private static final long serialVersionUID = -5747047661002140048L;

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
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  @Override
  protected void doProcess(ObjectAnnotationPanel panel, MouseEvent e) {
    LocatedObjects	objects;
    LocatedObjects	hits;
    SpreadSheet 	sheet;
    SpreadSheet		sheetHit;
    SpreadSheetDialog 	dialog;
    int[]		rows;

    objects  = new LocatedObjects(panel.getObjects());
    hits     = determineHits(panel, e);

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

      for (int row: rows) {
        if (isLoggingEnabled())
          getLogger().info("deleting: " + hits.get(row));
	objects.remove(hits.get(row));
      }
      panel.addUndoPoint("Deleting " + rows.length + " objects");
      panel.setObjects(objects);
      panel.annotationsChanged(this);
    }
  }
}
