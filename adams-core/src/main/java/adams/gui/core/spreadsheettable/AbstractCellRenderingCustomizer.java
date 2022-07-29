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
 * AbstractCellRenderingCustomizer.java
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.gui.core.SpreadSheetTable;

import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

/**
 * Ancestor for cell rendering customizers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCellRenderingCustomizer
  extends AbstractOptionHandler
  implements CellRenderingCustomizer {

  private static final long serialVersionUID = -4927739958774377498L;

  /**
   * For customizing the font of a cell.
   * <br>
   * Default implementation just returns the default.
   *
   * @param table	the table
   * @param isSelected	whether the cell is selected
   * @param hasFocus	whether the cell is focused
   * @param row		the current row
   * @param column	the current column
   * @param cell	the actual cell
   * @param defFont 	the default font
   * @return		the font
   */
  public Font getFont(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Font defFont) {
    return defFont;
  }


  /**
   * For customizing the foreground color of a cell.
   * <br>
   * Default implementation just returns the default.
   *
   * @param table	the table
   * @param isSelected	whether the cell is selected
   * @param hasFocus	whether the cell is focused
   * @param row		the current row
   * @param column	the current column
   * @param cell	the actual cell
   * @param defColor 	the default color
   * @return		the color
   */
  @Override
  public Color getForegroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor) {
    return defColor;
  }

  /**
   * For customizing the background color of a cell.
   * <br>
   * Default implementation just returns the default.
   *
   * @param table	the table
   * @param isSelected	whether the cell is selected
   * @param hasFocus	whether the cell is focused
   * @param row		the current row
   * @param column	the current column
   * @param cell	the actual cell
   * @param defColor 	the default color
   * @return		the color
   */
  @Override
  public Color getBackgroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor) {
    return defColor;
  }

  /**
   * For customizing the tooltip text.
   * <br>
   * Default implementation just returns the default.
   *
   * @param table	the table
   * @param isSelected	whether the cell is selected
   * @param hasFocus	whether the cell is focused
   * @param row		the current row
   * @param column	the current column
   * @param cell	the actual cell
   * @param defTip 	the default tip text
   * @return		the tip text
   */
  @Override
  public String getToolTipText(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, String defTip) {
    return defTip;
  }

  /**
   * For customizing the horizontal alignment of a cell.
   * <br>
   * Default implementation just returns the default.
   *
   * @param table	the table
   * @param isSelected	whether the cell is selected
   * @param hasFocus	whether the cell is focused
   * @param row		the current row
   * @param column	the current column
   * @param cell	the actual cell
   * @param defAlign 	the default alignment
   * @return		the alignment
   */
  @Override
  public int getHorizontalAlignment(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, int defAlign) {
    if ((column == 0) && table.getShowRowColumn())
      return SwingConstants.CENTER;
    return defAlign;
  }
}
