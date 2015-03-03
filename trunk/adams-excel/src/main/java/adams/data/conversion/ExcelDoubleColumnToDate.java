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
 * ExcelDoubleColumnToDate.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adams.core.DateUtils;
import adams.data.spreadsheet.Cell;

/**
 <!-- globalinfo-start -->
 * Converts the specified double column to a date column.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The double column to convert to date; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; apart from column names (case-sensitive), the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExcelDoubleColumnToDate
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 8681800940519018023L;

  /** the calendar to use. */
  protected Calendar m_Calendar;
  
  /** the start date. */
  protected Date m_StartDate;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified double column to a date column.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The double column to convert to date; " + m_Column.getExample();
  }

  /**
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String checkData() {
    m_Calendar  = new GregorianCalendar();
    m_StartDate = DateUtils.getDateFormatter().parse("1900-01-01");
    return super.checkData();
  }
  
  /**
   * Converts the cell's content to a new format.
   * 
   * @param cellOld	the current cell
   * @param cellNew	the new cell with the converted content
   * @throws Exception	if conversion fails
   */
  @Override
  protected void convert(Cell cellOld, Cell cellNew) throws Exception {
    m_Calendar.setTime((Date) m_StartDate.clone());
    m_Calendar.add(Calendar.DAY_OF_YEAR, cellOld.toDouble().intValue() - 1);  // we start on the 1st rather than 0th
    cellNew.setContent(m_Calendar.getTime());
  }
}
