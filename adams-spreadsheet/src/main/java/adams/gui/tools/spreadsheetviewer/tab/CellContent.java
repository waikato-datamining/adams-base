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
 * CellContent.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.dialog.TextPanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

import java.awt.BorderLayout;

/**
 * Displays the content of the selected cell.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CellContent
  extends AbstractViewerTab
  implements SelectionAwareViewerTab {

  /** for serialization. */
  private static final long serialVersionUID = -4215008790991120558L;
  
  /** for displaying the text. */
  protected TextPanel m_TextPanel;
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextPanel = new TextPanel();
    add(m_TextPanel, BorderLayout.CENTER);
  }
  
  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Cell content";
  }

  /**
   * Notifies the tab of the currently sheet.
   *
   * @param panel	the selected sheet panel
   * @param state	the table state
   */
  @Override
  public void sheetSelectionChanged(SpreadSheetPanel panel, TableState state) {
    String content;

    if (panel == null)
      return;

    content = "";
    if ((state.selRow != -1) && (state.selCol != -1))
      content = "" + state.table.getValueAt(state.selRow, state.selCol);

    m_TextPanel.setContent(content);
  }
}
