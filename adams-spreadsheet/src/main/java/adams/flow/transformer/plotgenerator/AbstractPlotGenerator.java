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

/**
 * AbstractPlotGenerator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import java.util.List;

import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.sink.SequencePlotter;

/**
 * Ancestor for generators that use data from a spreadsheet to create
 * plot containers for the {@link SequencePlotter} sink.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "default-cell-value", "defaultCellValue",
	    -1.0);
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks the spreadsheet.
   * <p/>
   * Default implementation only checks whether any data was provided.
   * 
   * @param sheet	the sheet to check
   */
  protected void check(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalStateException("No spreadsheet provided!");
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
	result = new Double(Utils.toDouble(cell.getContent()));
      else if (cell.isTime())
	result = new Double(cell.toTime().getTime());
      else if (cell.isDate())
	result = new Double(cell.toDate().getTime());
      else if (cell.isDateTime())
	result = new Double(cell.toDateTime().getTime());
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

    cell = row.getCell(index);
    if ((cell != null) && !cell.isMissing())
      result = cell.getNative();

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
