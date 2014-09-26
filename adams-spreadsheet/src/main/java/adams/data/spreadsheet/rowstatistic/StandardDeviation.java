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
 * StandardDeviation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Calculates the standard deviation (population or sample).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-is-sample &lt;boolean&gt; (property: isSample)
 * &nbsp;&nbsp;&nbsp;If set to true, the columns are treated as samples and not as populations.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class StandardDeviation
  extends AbstractDoubleArrayRowStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 2141252366056112668L;

  /** whether the arrays are samples or populations. */
  protected boolean m_IsSample;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the standard deviation (population or sample).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "is-sample", "isSample",
	    true);
  }

  /**
   * Sets whether the columns represent samples instead of populations.
   *
   * @param value	true if columns are samples and not populations
   */
  public void setIsSample(boolean value) {
    m_IsSample = value;
    reset();
  }

  /**
   * Returns whether the columns represent samples instead of populations.
   *
   * @return		true if columns are samples and not populations
   */
  public boolean getIsSample() {
    return m_IsSample;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String isSampleTipText() {
    return "If set to true, the columns are treated as samples and not as populations.";
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param rowIndex	the row index
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet postVisit(SpreadSheet sheet, int rowIndex) {
    SpreadSheet	result;
    Row		row;

    result = createOutputHeader();

    row = result.addRow();
    row.addCell(0).setContent("StdDev" + (getIsSample() ? "" : "P"));
    row.addCell(1).setContent(StatUtils.stddev(m_Values.toArray(), getIsSample()));

    m_Values = null;
    
    return result;
  }
}
