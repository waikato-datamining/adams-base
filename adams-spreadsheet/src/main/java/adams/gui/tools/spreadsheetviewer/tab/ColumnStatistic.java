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
 * ColumnStatistic.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.colstatistic.AbstractColumnStatistic;
import adams.data.spreadsheet.colstatistic.Sum;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TableRowRange;
import adams.gui.core.spreadsheettable.SpreadSheetTablePopupMenuItemHelper.TableState;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

import java.awt.BorderLayout;

/**
 * Displays the specified statistics from the selected column and rows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColumnStatistic
  extends AbstractViewerTab
  implements SelectionAwareViewerTab {

  /** for serialization. */
  private static final long serialVersionUID = -4215008790991120558L;

  /** the panel for defining the statistics. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the table with the statistics. */
  protected SortableAndSearchableTable m_Table;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());

    m_PanelGOE = new GenericObjectEditorPanel(AbstractColumnStatistic.class, new Sum(), true);
    add(m_PanelGOE, BorderLayout.NORTH);

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
    return "Column stats";
  }

  /**
   * Notifies the tab of the currently sheet.
   *
   * @param panel	the selected sheet panel
   * @param state	the table state
   */
  @Override
  public void sheetSelectionChanged(SpreadSheetPanel panel, TableState state) {
    SpreadSheetTableModel   	model;
    AbstractColumnStatistic 	statistic;
    SpreadSheet			data;
    SpreadSheet 		calculated;

    if (panel == null)
      return;

    model = new SpreadSheetTableModel();
    if ((state.selCol != -1) && (state.selRows.length > 0)) {
      data       = state.table.toSpreadSheet(TableRowRange.SELECTED, true);
      statistic  = (AbstractColumnStatistic) m_PanelGOE.getCurrent();
      calculated = statistic.generate(data, state.actCol);
      if (calculated != null)
	model = new SpreadSheetTableModel(calculated);
    }

    model.setUseSimpleHeader(true);
    model.setShowRowColumn(false);
    m_Table.setModel(model);
 }
}
