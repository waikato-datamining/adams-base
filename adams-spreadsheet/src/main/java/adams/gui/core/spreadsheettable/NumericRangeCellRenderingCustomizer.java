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
 * NumericRangeCellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.base.BaseInterval;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.gui.core.SpreadSheetTable;

import java.awt.Color;

/**
 * Uses the defined ranges to determine whether a numeric cells gets highlighted
 * with the specified color.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NumericRangeCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = 9158498328244673972L;

  /** the columns to work on. */
  protected SpreadSheetColumnRange m_Columns;

  /** the intervals. */
  protected BaseInterval[] m_Ranges;

  /** the color to use for highlighting. */
  protected Color m_Highlight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the defined ranges to determine whether a numeric cells gets highlighted with the specified color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));

    m_OptionManager.add(
      "range", "ranges",
      new BaseInterval[]{new BaseInterval(BaseInterval.ALL)});

    m_OptionManager.add(
      "highlight", "highlight",
      Color.RED);
  }

  /**
   * Sets the range of columns to apply the renderer to.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the range of columns to apply the renderer to.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String columnsTipText() {
    return "The columns to apply the renderer to; " + m_Columns.getExample();
  }

  /**
   * Sets the intervals.
   *
   * @param value	the intervals
   */
  public void setRanges(BaseInterval[] value) {
    m_Ranges = value;
    reset();
  }

  /**
   * Returns the intervals.
   *
   * @return		the intervals
   */
  public BaseInterval[] getRanges() {
    return m_Ranges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rangesTipText() {
    return "The intervals to use for matching numeric values.";
  }

  /**
   * Sets the color to use as background for the matching cells.
   *
   * @param value	the color
   */
  public void setHighlight(Color value) {
    m_Highlight = value;
    reset();
  }

  /**
   * Returns the color to use as background for the matching cells.
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
    return "The color to use as background for the matching cells.";
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
    double	value;

    if ((column == 0) && table.getShowRowColumn())
      return super.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, defColor);
    if ((cell == null) || !cell.isNumeric())
      return defColor;

    if (table.getShowRowColumn())
      column--;
    m_Columns.setData(table.toSpreadSheet());
    if (!m_Columns.isInRange(column))
      return defColor;

    value = cell.toDouble();
    for (BaseInterval range: m_Ranges) {
      if (range.isInside(value)) {
        if (isSelected)
	  return m_Highlight.darker();
        else
	  return m_Highlight;
      }
    }

    return defColor;
  }
}
