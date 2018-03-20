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
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;

import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

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

  /** whether to color background based on value. */
  protected boolean m_ValueBasedBackground;

  /** the color provider to use for the background. */
  protected AbstractColorGradientGenerator m_BackgroundColorGenerator;

  /** the color values (starting at 0). */
  protected transient Map<Integer,Color> m_Colors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified color to highlight the cells on the diagonal.\n"
      + "It also possible to color in the backgrounds of the non-diagonal cells based on their value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "highlight", "highlight",
      new Color(192, 192, 192, 128));

    m_OptionManager.add(
      "value-based-background", "valueBasedBackground",
      false);

    m_OptionManager.add(
      "background-color-generator", "backgroundColorGenerator",
      new BiColorGenerator());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Colors = null;
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
   * Sets whether to color in backgrounds based on their values.
   *
   * @param value	true if to color in background
   */
  public void setValueBasedBackground(boolean value) {
    m_ValueBasedBackground = value;
    reset();
  }

  /**
   * Returns whether to color in backgrounds based on their values.
   *
   * @return		true if to color in background
   */
  public boolean getValueBasedBackground() {
    return m_ValueBasedBackground;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String valueBasedBackgroundTipText() {
    return "If enabled, the background of the cells gets colored in based on their value.";
  }

  /**
   * Sets the color generator for obtaining the colors used for coloring in
   * the background.
   *
   * @param value	the generator
   */
  public void setBackgroundColorGenerator(AbstractColorGradientGenerator value) {
    m_BackgroundColorGenerator = value;
    reset();
  }

  /**
   * Returns the color generator for obtaining the colors used for coloring
   * in the background.
   *
   * @return		the generator
   */
  public AbstractColorGradientGenerator getBackgroundColorGenerator() {
    return m_BackgroundColorGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String backgroundColorGeneratorTipText() {
    return "The color generator to use for obtaining the colors for coloring in the backgrounds.";
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
   * Initializes the color lookup table.
   */
  protected synchronized void initColors() {
    Color[]	colors;
    int		i;

    if (m_Colors != null)
      return;

    m_Colors = new HashMap<>();
    colors = m_BackgroundColorGenerator.generate();
    for (i = 0; i < colors.length; i++)
      m_Colors.put(i, colors[i]);
  }

  /**
   * Determines min/max values in the table.
   *
   * @param table	the table to analyze
   * @return		the min and max
   */
  protected double[] getMinMax(SpreadSheetTable table) {
    double[]	result;
    SpreadSheet	sheet;
    int		r;
    int		c;
    Cell	cell;
    double	value;
    boolean	any;

    result  = new double[]{Double.MAX_VALUE, Double.MIN_VALUE};
    sheet   = table.toSpreadSheet();
    any     = false;
    for (r = 0; r < sheet.getRowCount(); r++) {
      for (c = 1; c < sheet.getColumnCount(); c++) {
        cell = sheet.getCell(r, c);
        if ((cell != null) && !cell.isMissing() && cell.isNumeric()) {
	  value     = cell.toDouble();
	  result[0] = Math.min(result[0], value);
	  result[1] = Math.max(result[1], value);
	  any       = true;
	}
      }
    }

    if (!any) {
      result[0] = 0;
      result[1] = 0;
    }

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
    int		actCol;
    int		actRow;
    double[]	minMax;
    double	min;
    double	max;
    double	value;
    int		index;

    initColors();

    result = super.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, defColor);
    actCol = column;
    actRow = row;
    if ((actCol == 0) && table.getShowRowColumn())
      return result;

    if (table.getShowRowColumn())
      actCol--;

    // first column is the actual label
    if (actCol == 0)
      return table.getTableHeader().getBackground();
    actCol--;

    actRow = table.getActualRow(actRow);
    if (actCol == actRow) {
      if (isSelected)
	result = m_Highlight.darker();
      else
	result = m_Highlight;
    }
    else if (m_ValueBasedBackground) {
      minMax = getMinMax(table);
      min    = minMax[0];
      max    = minMax[1];
      if (min < max) {
	value = table.getCellAt(row, column).toDouble();
	index = (int) ((value - min) / (max - min) * (m_Colors.size() - 1));
	result = m_Colors.get(index);
      }
    }

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

    result = super.getHorizontalAlignment(table, isSelected, hasFocus, row, column, cell, defAlign);
    if ((column == 0) && table.getShowRowColumn())
      return result;

    if (table.getShowRowColumn())
      column--;
    if (column == 0)
      return SwingConstants.LEFT;

    return result;
  }
}
