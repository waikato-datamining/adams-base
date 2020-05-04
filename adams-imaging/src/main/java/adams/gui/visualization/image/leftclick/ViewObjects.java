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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.leftclick;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Dialog.ModalityType;
import java.awt.Point;

/**
 * Displays the annotated objects in a dialog that contain the click position.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ViewObjects
  extends AbstractLeftClickProcessor {

  private static final long serialVersionUID = -5747047661002140048L;

  /** the prefixes for the objects. */
  protected BaseString[] m_Prefixes;

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
      "prefix", "prefixes",
      getDefaultPrefixes());
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  protected BaseString[] getDefaultPrefixes() {
    return new BaseString[]{new BaseString("Object.")};
  }

  /**
   * Sets the prefix to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setPrefixes(BaseString[] value) {
    m_Prefixes = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects.
   *
   * @return 		the prefix
   */
  public BaseString[] getPrefixes() {
    return m_Prefixes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixesTipText() {
    return "The prefix(es) to use for the fields in the report of the image.";
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
    SpreadSheet		sheet;
    SpreadSheet		sheetHit;
    SpreadSheetDialog 	dialog;

    objects  = LocatedObjects.fromReport(panel.getAllProperties(), BaseObject.toStringArray(m_Prefixes));
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
