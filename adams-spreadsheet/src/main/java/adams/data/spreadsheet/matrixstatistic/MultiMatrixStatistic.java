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
 * MultiMatrixStatistic.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.matrixstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Combines the statistics calculated from the specified statistic generators.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The rows of the subset to retrieve.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns of the subset to retrieve; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; column names (case-sensitive) as well as the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used: first, second, third, last_2, last_1,
 * &nbsp;&nbsp;&nbsp; last; numeric indices can be enforced by preceding them with '#' (eg '#12'
 * &nbsp;&nbsp;&nbsp;); column names can be surrounded by double quotes.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-statistic &lt;adams.data.spreadsheet.matrixstatistic.AbstractMatrixStatistic&gt; [-statistic ...] (property: statistics)
 * &nbsp;&nbsp;&nbsp;The statistics to calculate.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiMatrixStatistic
  extends AbstractMatrixStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 2141252366056112668L;

  /** the statistics to calculate. */
  protected AbstractMatrixStatistic[] m_Statistics;

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
	    new AbstractMatrixStatistic[0]);
  }

  /**
   * Sets the statistic generators to use.
   *
   * @param value	the generators
   */
  public void setStatistics(AbstractMatrixStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistic generators in use.
   *
   * @return		the generators
   */
  public AbstractMatrixStatistic[] getStatistics() {
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
   * Performs the actual generation of statistics for the specified
   * spreadsheet.
   *
   * @param sheet	the spreadsheet subset to generate the stats for
   * @return		the generated statistics, null in case of an error
   */
  @Override
  protected SpreadSheet doGenerate(SpreadSheet sheet) {
    SpreadSheet			result;
    int				i;
    AbstractMatrixStatistic 	stat;
    SpreadSheet			sub;

    result = createOutputHeader();
    for (i = 0; i < m_Statistics.length; i++) {
      stat = m_Statistics[i];
      sub  = stat.generate(sheet);
      if (stat.hasLastError()) {
        if (!hasLastError())
          m_LastError = "#" + (i+1) + ": " + stat.getLastError();
        else
          m_LastError += "\n#" + (i+1) + ": " + stat.getLastError();
      }
      if ((sub == null) || stat.hasLastError())
	continue;
      for (Row row: sub.rows())
	result.addRow().assign(row);
    }
    
    return result;
  }
}
