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
 * MultiCellRenderingCustomizer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.Cell;
import adams.gui.core.SpreadSheetTable;

import java.awt.Color;

/**
 * Applies the sub-renderers sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = 2309521459919868974L;

  /** the sub-renderers to use. */
  protected CellRenderingCustomizer[] m_Renderers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the renderers sequentially, with the output being the default for the next one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "renderer", "renderers",
      new CellRenderingCustomizer[0]);
  }

  /**
   * Sets the sub-renderers.
   *
   * @param value	the renderers
   */
  public void setRenderers(CellRenderingCustomizer[] value) {
    m_Renderers = value;
    reset();
  }

  /**
   * Returns the sub-renderers.
   *
   * @return		the renderers
   */
  public CellRenderingCustomizer[] getRenderers() {
    return m_Renderers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public  String renderersTipText() {
    return "The sub-renderers to apply sequentially.";
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
    Color	result;

    result = defColor;
    for (CellRenderingCustomizer renderer: m_Renderers)
      result = renderer.getForegroundColor(table, isSelected, hasFocus, row, column, cell, result);

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

    result = defColor;
    for (CellRenderingCustomizer renderer: m_Renderers)
      result = renderer.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, result);

    return result;
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
    String	result;

    result = defTip;
    for (CellRenderingCustomizer renderer: m_Renderers)
      result = renderer.getToolTipText(table, isSelected, hasFocus, row, column, cell, result);

    return result;
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
    int		result;

    result = defAlign;
    for (CellRenderingCustomizer renderer: m_Renderers)
      result = renderer.getHorizontalAlignment(table, isSelected, hasFocus, row, column, cell, result);

    return result;
  }
}
