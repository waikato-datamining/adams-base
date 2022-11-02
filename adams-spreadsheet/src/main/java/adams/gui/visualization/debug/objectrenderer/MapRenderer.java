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
 * MapRenderer.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
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
 */
public class MapRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the last setup. */
  protected SpreadSheetPanel m_LastSheetPanel;

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
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  @Override
  public boolean canRenderCached(Object obj, JPanel panel) {
    return (m_LastSheetPanel != null);
  }

  /**
   * Turns the map into a spreadsheet.
   *
   * @param map		the map to convert
   * @return		the generated spreadsheet
   */
  protected SpreadSheet mapToSheet(Map map) {
    SpreadSheet 	result;
    Row			row;

    result = new DefaultSpreadSheet();
    row   = result.getHeaderRow();
    row.addCell("K").setContentAsString("Key");
    row.addCell("V").setContentAsString("Value");
    for (Object key: map.keySet()) {
      row = result.addRow();
      row.addCell("K").setNative(key);
      row.addCell("V").setNative(map.get(key));
    }

    return result;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel, Integer limit) {
    m_LastSheetPanel.setSpreadSheet(mapToSheet((Map) obj));
    panel.add(m_LastSheetPanel, BorderLayout.CENTER);
    return null;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel, Integer limit) {
    SpreadSheet		sheet;
    SpreadSheetPanel    sheetPanel;

    sheet      = mapToSheet((Map) obj);
    sheetPanel = new SpreadSheetPanel();
    sheetPanel.setSpreadSheet(sheet);
    sheetPanel.setShowSearch(true);
    panel.add(sheetPanel, BorderLayout.CENTER);

    m_LastSheetPanel = sheetPanel;

    return null;
  }
}
