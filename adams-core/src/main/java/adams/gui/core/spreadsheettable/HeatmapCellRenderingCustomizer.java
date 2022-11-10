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
 * HeatmapCellRenderingCustomizer.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.SpreadSheetTable;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Determines the min/max of numeric values in the table and colors the
 * background using the specified generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HeatmapCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = 5621920609459687288L;

  /** the color provider to use for the background. */
  protected AbstractColorGradientGenerator m_BackgroundColorGenerator;

  /** the color values (starting at 0). */
  protected transient Map<Integer,Color> m_Colors;

  /** the last spreadsheet that was used. */
  protected transient SpreadSheet m_LastSpreadSheet;

  /** the last min/max values. */
  protected transient double[] m_LastMinMax;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Determines the min/max of numeric values in the table and colors "
      + "the background using the specified generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "background-color-generator", "backgroundColorGenerator",
      getDefaultBackgroundColorGenerator());
  }

  /**
   * Returns the default color generator.
   *
   * @return		the generator
   */
  protected AbstractColorGradientGenerator getDefaultBackgroundColorGenerator() {
    BiColorGenerator	result;

    result = new BiColorGenerator();
    result.setFirstColor(Color.WHITE);
    result.setSecondColor(Color.RED);

    return result;
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
    double	min;
    double	max;
    double	value;
    int		index;
    Cell	spCell;
    SpreadSheet sheet;

    initColors();

    result = super.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, defColor);
    actCol = column;
    if ((actCol == 0) && table.getShowRowColumn())
      return result;

    sheet = table.toSpreadSheet();
    if ((m_LastSpreadSheet == null) || (sheet != m_LastSpreadSheet))
      m_LastMinMax = null;
    if (m_LastMinMax == null)
      m_LastMinMax = SpreadSheetUtils.getMinMax(table, null, null);
    min = m_LastMinMax[0];
    max = m_LastMinMax[1];
    if (min < max) {
      spCell = table.getCellAt(row, column);
      if ((spCell != null) && spCell.isNumeric()) {
	value = spCell.toDouble();
	index = (int) ((value - min) / (max - min) * (m_Colors.size() - 1));
	result = m_Colors.get(index);
      }
      else {
	result = defColor;
      }
      if (isSelected)
	result = result.darker();
    }

    m_LastSpreadSheet = sheet;

    return result;
  }
}
