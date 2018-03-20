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
 * SpreadSheetCellRenderer.java
 * Copyright (C) 2009-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.data.spreadsheet.Cell;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * Custom cell renderer for displaying spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCellRenderer
  extends DefaultTableCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = -6070112998601610760L;

  /**
   * Returns the default table cell renderer.
   *
   * @param table		the table this object belongs to
   * @param value		the actual cell value
   * @param isSelected		whether the cell is selected
   * @param hasFocus		whether the cell has the focus
   * @param row			the row in the table
   * @param column		the column in the table
   * @return			the rendering component
   */
  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column ) {

    Component 			result;
    Cell			cell;
    SpreadSheetTable		spTable;
    CellRenderingCustomizer	rend;
    int				align;

    result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    spTable = (SpreadSheetTable) table;
    rend    = spTable.getCellRenderingCustomizer();
    cell    = spTable.getCellAt(row, column);
    align   = SwingConstants.LEFT;
    if ((cell != null) && cell.isNumeric())
      align = SwingConstants.RIGHT;

    ((JLabel) result).setHorizontalAlignment(rend.getHorizontalAlignment(spTable, isSelected, hasFocus, row, column, cell, align));
    ((JLabel) result).setToolTipText(rend.getToolTipText(spTable, isSelected, hasFocus, row, column, cell, null));
    result.setForeground(rend.getForegroundColor(spTable, isSelected, hasFocus, row, column, cell, (isSelected ? table.getSelectionForeground() : table.getForeground())));
    result.setBackground(rend.getBackgroundColor(spTable, isSelected, hasFocus, row, column, cell, (isSelected ? table.getSelectionBackground() : table.getBackground())));
    result.setFont(rend.getFont(spTable, isSelected, hasFocus, row, column, cell, result.getFont()));

    return result;
  }
}