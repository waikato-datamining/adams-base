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
 * MultiColumnStatistic.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.colstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Combines the statistics calculated from the specified statistic generators.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.spreadsheet.statistic.AbstractColumnStatistic&gt; [-statistic ...] (property: statistics)
 * &nbsp;&nbsp;&nbsp;The statistics to calculate.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiColumnStatistic
  extends AbstractColumnStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 2141252366056112668L;

  /** the statistics to calculate. */
  protected AbstractColumnStatistic[] m_Statistics;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the statistics calculated from the specified statistic generators.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "statistic", "statistics",
	    new AbstractColumnStatistic[0]);
  }

  /**
   * Sets the statistic generators to use.
   *
   * @param value	the generators
   */
  public void setStatistics(AbstractColumnStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistic generators in use.
   *
   * @return		the generators
   */
  public AbstractColumnStatistic[] getStatistics() {
    return m_Statistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticsTipText() {
    return "The statistics to calculate.";
  }

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   */
  @Override
  protected void preVisit(SpreadSheet sheet, int colIndex) {
    for (AbstractColumnStatistic stat: m_Statistics)
      stat.preVisit(sheet, colIndex);
  }

  /**
   * Gets called with every row in the spreadsheet for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  @Override
  protected void doVisit(Row row, int colIndex) {
    for (AbstractColumnStatistic stat: m_Statistics)
      stat.doVisit(row, colIndex);
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet postVisit(SpreadSheet sheet, int colIndex) {
    SpreadSheet		result;
    SpreadSheet		sub;

    result = createOutputHeader();
    for (AbstractColumnStatistic stat: m_Statistics) {
      sub = stat.postVisit(sheet, colIndex);
      if (sub == null)
	continue;
      for (Row row: sub.rows())
	result.addRow().assign(row);
    }
    
    return result;
  }
}
