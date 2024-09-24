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
 * AbstractPlotGenerator.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.sink.SequencePlotter;

import java.util.List;

/**
 * Ancestor for generators that use data from a spreadsheet to create
 * plot containers for the {@link SequencePlotter} sink.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPlotGenerator
  extends AbstractOptionHandler 
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -7535085726098063330L;

  /** the default string value for cells that are missing. */
  public final static String MISSING_CELL_VALUE = "MISSING";

  /** the default value for missing or non-numeric cells. */
  protected double m_DefaultCellValue;

  /** the columns that make up the plot name. */
  protected SpreadSheetColumnRange m_PlotNameRange;

  /** the separator for the plot names. */
  protected String m_PlotNameSeparator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "default-cell-value", "defaultCellValue",
      -1.0);

    m_OptionManager.add(
      "plot-name-range", "plotNameRange",
      "");

    m_OptionManager.add(
      "plot-name-separator", "plotNameSeparator",
      "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PlotNameRange = new SpreadSheetColumnRange();
  }

  /**
   * Sets the default value for missing or non-numeric cells.
   *
   * @param value	the default value
   */
  public void setDefaultCellValue(double value) {
    m_DefaultCellValue = value;
    reset();
  }

  /**
   * Returns the default value for missing or non-numeric cells.
   *
   * @return		the default value
   */
  public double getDefaultCellValue() {
    return m_DefaultCellValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultCellValueTipText() {
    return "The default value for missing or non-numeric cells.";
  }

  /**
   * Sets the column range to use generating the plot name.
   *
   * @param value	the column range
   */
  public void setPlotNameRange(String value) {
    m_PlotNameRange.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to use generating the plot name.
   *
   * @return		the column range
   */
  public String getPlotNameRange() {
    return m_PlotNameRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameRangeTipText() {
    return "The range of columns to use for generating the plot name (overrides any plot generator specific names); " + m_PlotNameRange.getExample();
  }

  /**
   * Sets the separator to use when constructing the plot name from cell values.
   *
   * @param value	the separator
   */
  public void setPlotNameSeparator(String value) {
    m_PlotNameSeparator = value;
    reset();
  }

  /**
   * Returns the separator to use when constructing the plot name from cell values.
   *
   * @return		the separator
   */
  public String getPlotNameSeparator() {
    return m_PlotNameSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameSeparatorTipText() {
    return "The separator to use when constructing the plot name from cell values.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "plotNameRange", (getPlotNameRange().isEmpty() ? "-default-" : getPlotNameRange()), "plot: ");
  }

  /**
   * Checks the spreadsheet.
   * <br><br>
   * Default implementation only checks whether any data was provided.
   * 
   * @param sheet	the sheet to check
   */
  protected void check(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalStateException("No spreadsheet provided!");
    m_PlotNameRange.setData(sheet);
  }

  /**
   * Returns the string value for the specified cell.
   *
   * @param row		the row to get the cell from
   * @param index	the column index
   * @return		the string value
   */
  protected String getCellString(Row row, int index) {
    String	result;
    Cell	cell;

    result = MISSING_CELL_VALUE;

    cell = row.getCell(index);
    if ((cell != null) && !cell.isMissing())
      result = cell.getContent();

    return result;
  }

  /**
   * Returns the cell value for the specified column index.
   * Uses the default value if the cell is missing.
   *
   * @param row		the row to get the cell from
   * @param index	the column index
   * @return		the cell value
   * @see		#m_DefaultCellValue
   */
  protected Comparable getCellValue(Row row, int index, Comparable defaultValue) {
    Comparable	result;
    Cell	cell;

    result = defaultValue;

    cell = row.getCell(index);
    if ((cell != null) && !cell.isMissing()) {
      if (cell.isNumeric())
	result = Utils.toDouble(cell.getContent());
      else if (cell.isTime())
	result = (double) cell.toTime().getTime();
      else if (cell.isDate())
	result = (double) cell.toDate().getTime();
      else if (cell.isDateTime())
	result = (double) cell.toDateTime().getTime();
      else
	result = cell.getContent();
    }

    return result;
  }

  /**
   * Returns the cell value for the specified column index.
   * Uses the default value if the cell is missing.
   *
   * @param row		the row to get the cell from
   * @param index	the column index
   * @return		the cell value
   * @see		#m_DefaultCellValue
   */
  protected Object getCellObject(Row row, int index, Object defaultValue) {
    Object	result;
    Cell	cell;

    result = defaultValue;

    if (row.hasCell(index)) {
      cell = row.getCell(index);
      if ((cell != null) && !cell.isMissing())
	result = cell.getNative();
    }

    return result;
  }

  /**
   * Returns the cell value for the specified column index.
   * Uses the default value if the cell is missing.
   *
   * @param row		the row to get the cell from
   * @param index	the column index
   * @return		the cell value
   * @see		#m_DefaultCellValue
   */
  protected Comparable getCellValue(Row row, int index) {
    return getCellValue(row, index, m_DefaultCellValue);
  }

  /**
   * Returns the plot name to use.
   *
   * @param row		the row to construct the name from, if necessary
   * @param defValue    the default plot name
   * @return		the plot name
   */
  protected String getActualPlotName(Row row, String defValue) {
    String      result;
    int[]       indices;
    int         i;

    result = defValue;

    if (!getPlotNameRange().isEmpty()) {
      indices = m_PlotNameRange.getIntIndices();
      result  = "";
      for (i = 0; i < indices.length; i++) {
        if (i > 0)
          result += m_PlotNameSeparator;
        result += getCellString(row, indices[i]);
      }
    }

    return result;
  }

  /**
   * Performs the actual generation of containers.
   * 
   * @param sheet	the basis for the containers
   * @return		the generated containers
   */
  protected abstract List<SequencePlotterContainer> doGenerate(SpreadSheet sheet);
  
  /**
   * Generates plot containers from the provided spreadsheet.
   * 
   * @param sheet	the data to use
   * @return		the generated containers
   */
  public List<SequencePlotterContainer> generate(SpreadSheet sheet) {
    check(sheet);
    return doGenerate(sheet);
  }
}
