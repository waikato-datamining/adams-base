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
 * DistinctValueCellRenderingCustomizer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.base.BaseString;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.SpreadSheetTable;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each distinct value in the spreadsheet gets its own color.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DistinctValueCellRenderingCustomizer
  extends AbstractCellRenderingCustomizer {

  private static final long serialVersionUID = 5621920609459687288L;

  /** the color provider to use for the background. */
  protected ColorProvider m_ColorProvider;

  /** the predefined values. */
  protected BaseString[] m_PredefinedValues;

  /** the value color mapping. */
  protected transient Map<String,Color> m_Colors;

  /** the last spreadsheet that was used. */
  protected transient SpreadSheet m_LastSpreadSheet;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Each distinct value in the spreadsheet gets its own background color.\n"
      + "When supplying predefined values, then only these values will get a background color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-provider", "colorProvider",
      getDefaultColorProvider());

    m_OptionManager.add(
      "predefined-value", "predefinedValues",
      new BaseString[0]);
  }

  /**
   * Returns the default color generator.
   *
   * @return		the generator
   */
  protected ColorProvider getDefaultColorProvider() {
    return new DefaultColorProvider();
  }

  /**
   * Sets the color provider to use for generating the distinct values.
   *
   * @param value	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for generating the distinct values.
   *
   * @return		the provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for generating the colors for the distinct values.";
  }

  /**
   * Sets the predefined values to highlight.
   *
   * @param value	the values
   */
  public void setPredefinedValues(BaseString[] value) {
    m_PredefinedValues = value;
    reset();
  }

  /**
   * Returns the predefined values to highlight.
   *
   * @return		the values
   */
  public BaseString[] getPredefinedValues() {
    return m_PredefinedValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String predefinedValuesTipText() {
    return "The predefined values to get background colors.";
  }

  /**
   * Initializes the color lookup table.
   *
   * @param sheet	the spreadsheet to use for initializing
   */
  protected synchronized void initColors(SpreadSheet sheet) {
    Color[]		colors;
    List<String> 	values;

    if (m_Colors != null)
      return;

    m_Colors = new HashMap<>();
    m_ColorProvider.resetColors();

    if (m_PredefinedValues.length > 0) {
      for (BaseString value: m_PredefinedValues)
        m_Colors.put(value.getValue(), m_ColorProvider.next());
    }
    else {
      values = SpreadSheetUtils.uniqueValues(sheet);
      for (String value: values)
	m_Colors.put(value, m_ColorProvider.next());
    }
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
    String	content;

    result = super.getBackgroundColor(table, isSelected, hasFocus, row, column, cell, defColor);
    actCol = column;
    if ((actCol == 0) && table.getShowRowColumn())
      return result;

    sheet = table.toSpreadSheet();
    if ((m_LastSpreadSheet == null) || (sheet != m_LastSpreadSheet))
      m_Colors = null;
    initColors(sheet);

    content = null;
    spCell  = table.getCellAt(row, column);
    if ((spCell != null) && !spCell.isMissing())
      content = spCell.getContent();
    result = m_Colors.getOrDefault(content, defColor);
    if (isSelected)
      result = result.darker();

    m_LastSpreadSheet = sheet;

    return result;
  }
}
