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
 * SpreadSheetRenderer.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetView;
import adams.gui.core.BaseButton;
import adams.gui.dialog.SpreadSheetPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Renders spreadsheets as tables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRenderer
    extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  public static final int MAX_ROWS = 100;

  /** the last setup. */
  protected SpreadSheetPanel m_LastPanel;

  /** the last limit. */
  protected Integer m_LastLimit;

  /**
   * Returns whether a limit is supported by the renderer.
   *
   * @param obj		the object to render
   * @return		true if supplying a limit has an effect
   */
  @Override
  public boolean supportsLimit(Object obj) {
    return true;
  }

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(SpreadSheet.class, cls);
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
    return (m_LastPanel != null);
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
    if (m_LastLimit != limit) {
      m_LastLimit = limit;
      return render(obj, panel, limit);
    }

    m_LastPanel.setSpreadSheet((SpreadSheet) obj);
    m_LastPanel.setShowSearch(true);
    panel.add(m_LastPanel, BorderLayout.CENTER);
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
    final SpreadSheet		sheet;
    SpreadSheet			view;
    final SpreadSheetPanel 	sheetPanel;
    final JPanel		panelButton;
    BaseButton			buttonAll;
    int				maxRows;

    maxRows = MAX_ROWS;
    if (limit != null)
      maxRows = (limit == -1 ? Integer.MAX_VALUE : limit);

    m_LastLimit = limit;

    sheet = (SpreadSheet) obj;
    if (sheet.getRowCount() >= maxRows) {
      sheetPanel = new SpreadSheetPanel();
      view       = new SpreadSheetView(sheet, 0, maxRows);
      sheetPanel.setSpreadSheet(view);
      sheetPanel.setShowSearch(true);
      panel.add(sheetPanel, BorderLayout.CENTER);
      panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelButton, BorderLayout.SOUTH);
      buttonAll = new BaseButton("Show all " + sheet.getRowCount() + " rows");
      buttonAll.addActionListener((ActionEvent e) -> {
	sheetPanel.setSpreadSheet(sheet);
	panelButton.setVisible(false);
	m_LastPanel = sheetPanel;
      });
      panelButton.add(buttonAll);
    }
    else {
      sheetPanel = new SpreadSheetPanel();
      sheetPanel.setSpreadSheet(sheet);
      sheetPanel.setShowSearch(true);
      panel.add(sheetPanel, BorderLayout.CENTER);
      m_LastPanel = sheetPanel;
    }

    return null;
  }
}
