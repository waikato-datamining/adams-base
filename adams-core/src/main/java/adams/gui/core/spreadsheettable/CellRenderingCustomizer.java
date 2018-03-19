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
 * CellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.option.OptionHandler;
import adams.data.spreadsheet.Cell;
import adams.gui.core.SpreadSheetTable;

import java.awt.Color;
import java.awt.Font;

/**
 * Interface for classes that customize the rendering of a cell.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface CellRenderingCustomizer
  extends OptionHandler {

  /**
   * For customizing the font of a cell.
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
  public Font getFont(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Font defFont);

  /**
   * For customizing the foreground color of a cell.
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
  public Color getForegroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor);

  /**
   * For customizing the background color of a cell.
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
  public Color getBackgroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor);

  /**
   * For customizing the tooltip text.
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
  public String getToolTipText(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, String defTip);

  /**
   * For customizing the horizontal alignment of a cell.
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
  public int getHorizontalAlignment(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, int defAlign);
}
