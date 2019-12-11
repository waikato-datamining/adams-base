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
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
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
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel) {
    final SpreadSheet		sheet;
    SpreadSheet			view;
    TIntList			rows;
    int				i;
    final SpreadSheetPanel 	sheetPanel;
    final JPanel		panelButton;
    BaseButton			buttonAll;

    sheet = (SpreadSheet) obj;
    if (sheet.getRowCount() >= MAX_ROWS) {
      rows = new TIntArrayList();
      for (i = 0; i < MAX_ROWS; i++)
        rows.add(i);
      sheetPanel = new SpreadSheetPanel();
      view       = new SpreadSheetView(sheet, rows.toArray(), null);
      sheetPanel.setSpreadSheet(view);
      sheetPanel.setShowSearch(true);
      panel.add(sheetPanel, BorderLayout.CENTER);
      panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelButton, BorderLayout.SOUTH);
      buttonAll = new BaseButton("Show all " + sheet.getRowCount() + " rows");
      buttonAll.addActionListener((ActionEvent e) -> {
	sheetPanel.setSpreadSheet(sheet);
	panelButton.setVisible(false);
      });
      panelButton.add(buttonAll);
    }
    else {
      sheetPanel = new SpreadSheetPanel();
      sheetPanel.setSpreadSheet(sheet);
      sheetPanel.setShowSearch(true);
      panel.add(sheetPanel, BorderLayout.CENTER);
    }

    return null;
  }
}
