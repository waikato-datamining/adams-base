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
 * MapRenderer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.dialog.SpreadSheetPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Map;

/**
 * Renders {@link Map} as tables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MapRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(Map.class, cls);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel) {
    Map			map;
    SpreadSheet		sheet;
    Row			row;
    SpreadSheetPanel    sheetPanel;

    map   = (Map) obj;
    sheet = new DefaultSpreadSheet();
    row   = sheet.getHeaderRow();
    row.addCell("K").setContentAsString("Key");
    row.addCell("V").setContentAsString("Value");
    for (Object key: map.keySet()) {
      row = sheet.addRow();
      row.addCell("K").setNative(key);
      row.addCell("V").setNative(map.get(key));
    }
    sheetPanel = new SpreadSheetPanel();
    sheetPanel.setSpreadSheet(sheet);
    sheetPanel.setShowSearch(true);
    panel.add(sheetPanel, BorderLayout.CENTER);

    return null;
  }
}
