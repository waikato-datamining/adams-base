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
 * ArrayStatistic.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.rowstatistic;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.AbstractArrayStatistic;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Pushes the numeric row through the specified array statistic.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.statistics.AbstractArrayStatistic&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The statistic to apply to the numeric row.
 * &nbsp;&nbsp;&nbsp;default: adams.data.statistics.ArrayHistogram
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class ArrayStatistic
  extends AbstractDoubleArrayRowStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 330391755072250767L;

  /** the array statistic to use. */
  protected AbstractArrayStatistic m_Statistic;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Pushes the numeric row through the specified array statistic.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistic",
      new ArrayHistogram<>());
  }

  /**
   * Sets the array statistic to apply.
   *
   * @param value	the statistic
   */
  public void setStatistic(AbstractArrayStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the array statistic to apply.
   *
   * @return		the statistic
   */
  public AbstractArrayStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to apply to the numeric row.";
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
    SpreadSheet		result;
    StatisticContainer cont;

    result = null;

    m_Statistic.clear();
    m_Statistic.add(StatUtils.toNumberArray(m_Values.toArray()));
    cont = m_Statistic.calculate();
    if (cont != null)
      result = cont.toSpreadSheet();

    m_Values = null;
    
    return result;
  }
}
