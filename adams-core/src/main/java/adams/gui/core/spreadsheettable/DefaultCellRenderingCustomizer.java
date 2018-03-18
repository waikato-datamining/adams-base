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
 * DefaultCellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.Cell;
import adams.gui.core.SpreadSheetTable;

import javax.swing.SwingConstants;
import java.awt.Color;

/**
 * Default cell rendering customizer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultCellRenderingCustomizer
  extends AbstractColoredCellRenderingCustomizer {

  private static final long serialVersionUID = 581759740968152666L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Default customizer.";
  }

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
  @Override
  public Color getForegroundColor(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, Color defColor) {
    return defColor;
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
    Double	numericVal;

    if (cell == null)
      return defColor;

    if ((column == 0) && table.getShowRowColumn())
      return defColor;

    numericVal = null;
    if (!(cell.isFormula() && table.getShowFormulas())) {
      if (cell.isNumeric() && (getUseBackgroundNegative() || getUseBackgroundPositive()))
	numericVal = cell.toDouble();
    }

    if (cell.isMissing()) {
      if (isSelected)
	return Color.GRAY;
      else
	return Color.LIGHT_GRAY;
    }
    else {
      if (isSelected) {
	if ((numericVal != null) && (numericVal >= 0) && (getUseBackgroundPositive()))
	  return getBackgroundPositive().darker();
	else if ((numericVal != null) && (numericVal < 0) && (getUseBackgroundNegative()))
	  return getBackgroundNegative().darker();
	else
	  return table.getSelectionBackground();
      }
      else {
	if ((numericVal != null) && (numericVal >= 0) && (getUseBackgroundPositive()))
	  return getBackgroundPositive();
	else if ((numericVal != null) && (numericVal < 0) && (getUseBackgroundNegative()))
	  return getBackgroundNegative();
	else
	  return table.getBackground();
      }
    }
  }

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
  @Override
  public String getToolTipText(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, String defTip) {
    if ((column == 0) && table.getShowRowColumn())
      return defTip;
    if ((cell != null) && cell.isMissing())
      return "missing";
    return defTip;
  }

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
  @Override
  public int getHorizontalAlignment(SpreadSheetTable table, boolean isSelected, boolean hasFocus, int row, int column, Cell cell, int defAlign) {
    boolean	numeric;

    if ((column == 0) && table.getShowRowColumn())
      return SwingConstants.CENTER;

    numeric = false;
    if (cell != null) {
      if (!(cell.isFormula() && table.getShowFormulas()))
	numeric = cell.isNumeric();
    }

    if (numeric)
      return SwingConstants.RIGHT;
    else
      return defAlign;
  }
}
