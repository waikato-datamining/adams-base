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
 * ConfusionMatrixCellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.Cell;
import adams.gui.core.SpreadSheetTable;

import java.awt.Color;
import java.awt.Font;

/**
 * Uses the specified color to highlight the cells on the diagonal.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfusionMatrixCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = -5036118898148310042L;

  /** the color to use for highlighting. */
  protected Color m_Highlight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified color to highlight the cells on the diagonal.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "highlight", "highlight",
      Color.LIGHT_GRAY);
  }

  /**
   * Sets the color to use as background for the diagonal cells.
   *
   * @param value	the color
   */
  public void setHighlight(Color value) {
    m_Highlight = value;
    reset();
  }

  /**
   * Returns the color to use as background for the diagonal cells.
   *
   * @return		the color
   */
  public Color getHighlight() {
    return m_Highlight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String highlightTipText() {
    return "The color to use as background for the diagonal cells.";
  }

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
  @Override
  public Font getFont(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Font defFont) {
    Font	result;

    result = super.getFont(table, isSelected, hasFocus, row, column, cell, defFont);
    if ((column == 0) && table.getShowRowColumn())
      return result;

    column--;
    if (column == 0)
      return result.deriveFont(Font.BOLD);

    return result;
  }

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
  @Override
  public Color getBackgroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor) {
    Color	result;

    result = super.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, defColor);
    if ((column == 0) && table.getShowRowColumn())
      return result;

    if (table.getShowRowColumn())
      column--;

    // first column is the actual label
    if (column == 0)
      return table.getTableHeader().getBackground();
    column--;

    if (column == row) {
      if (isSelected)
	result = m_Highlight.darker();
      else
	result = m_Highlight;
    }

    return result;
  }
}
