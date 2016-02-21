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
 * Information.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

import java.awt.BorderLayout;

/**
 * Simple information tab.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Information
  extends AbstractViewerTab
  implements SelectionAwareViewerTab {

  /** for serialization. */
  private static final long serialVersionUID = -4215008790991120558L;
  
  /** the table with the information. */
  protected SortableAndSearchableTable m_Table;
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_Table = new SortableAndSearchableTable();
    m_Table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_ALL_COLUMNS);
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
  }
  
  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Info";
  }

  /**
   * Notifies the tab of the currently sheet.
   *
   * @param panel	the selected sheet panel
   * @param rows	the selected rows
   */
  @Override
  public void sheetSelectionChanged(SpreadSheetPanel panel, int[] rows) {
    SpreadSheet			sheet;
    SpreadSheet			info;
    Row				row;
    SpreadSheetTableModel	model;
    
    sheet = panel.getSheet();

    info  = new DefaultSpreadSheet();
    row   = info.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");
    
    if (sheet.getName() != null) {
      row = info.addRow();
      row.addCell("K").setContent("Name");
      row.addCell("V").setContent(sheet.getName());
    }

    row = info.addRow();
    row.addCell("K").setContent("Rows");
    row.addCell("V").setContent(sheet.getRowCount());

    row = info.addRow();
    row.addCell("K").setContent("Columns");
    row.addCell("V").setContent(sheet.getColumnCount());

    row = info.addRow();
    row.addCell("K").setContent("Selected rows");
    row.addCell("V").setContent(rows.length);
    
    model = new SpreadSheetTableModel(info);
    model.setUseSimpleHeader(true);
    model.setShowRowColumn(false);
    m_Table.setModel(model);
  }
}
