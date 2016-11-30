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
 * SpreadSheetCellRenderer.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.data.spreadsheet.Cell;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Custom cell renderer for displaying spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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

    Component 		result;
    Cell		cell;
    SpreadSheetTable	spTable;
    boolean		numeric;
    Double		numericVal;

    result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    spTable    = (SpreadSheetTable) table;
    cell       = spTable.getCellAt(row, column);
    numeric    = false;
    numericVal = null;

    // row index
    if ((column == 0) && spTable.getShowRowColumn()) {
      ((JLabel) result).setHorizontalAlignment(SwingConstants.CENTER);
      ((JLabel) result).setToolTipText(null);
      if (isSelected)
	((JLabel) result).setBackground(spTable.getSelectionBackground());
      else
        ((JLabel) result).setBackground(spTable.getBackground());
      return result;
    }
    
    if (cell != null) {
      if (!(cell.isFormula() && spTable.getShowFormulas())) {
	numeric = cell.isNumeric();
	if (numeric && (spTable.hasNegativeBackground() || spTable.hasPositiveBackground()))
	  numericVal = cell.toDouble();
      }
      
      // background and hint
      if (cell.isMissing()) {
        ((JLabel) result).setToolTipText("missing");
        if (isSelected)
          ((JLabel) result).setBackground(Color.GRAY);
        else
          ((JLabel) result).setBackground(Color.LIGHT_GRAY);
      }
      else {
        ((JLabel) result).setToolTipText(null);
        if (isSelected) {
          if ((numericVal != null) && (numericVal >= 0) && (spTable.hasPositiveBackground()))
            ((JLabel) result).setBackground(spTable.getPositiveBackground().darker());
          else if ((numericVal != null) && (numericVal < 0) && (spTable.hasNegativeBackground()))
            ((JLabel) result).setBackground(spTable.getNegativeBackground().darker());
          else
            ((JLabel) result).setBackground(spTable.getSelectionBackground());
        }
        else {
          if ((numericVal != null) && (numericVal >= 0) && (spTable.hasPositiveBackground()))
            ((JLabel) result).setBackground(spTable.getPositiveBackground());
          else if ((numericVal != null) && (numericVal < 0) && (spTable.hasNegativeBackground()))
            ((JLabel) result).setBackground(spTable.getNegativeBackground());
          else
            ((JLabel) result).setBackground(spTable.getBackground());
        }
      }

      // alignment
      if (numeric)
        ((JLabel) result).setHorizontalAlignment(SwingConstants.RIGHT);
      else
        ((JLabel) result).setHorizontalAlignment(SwingConstants.LEFT);
    }
    else {
      // alignment
      ((JLabel) result).setToolTipText("missing");
      if (isSelected)
	((JLabel) result).setBackground(Color.GRAY);
      else
	((JLabel) result).setBackground(Color.LIGHT_GRAY);
    }

    return result;
  }
}