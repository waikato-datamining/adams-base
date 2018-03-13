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
 * Percentile.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.rowstatistic;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Calculates the specified percentile.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-percentile &lt;double&gt; (property: percentile)
 * &nbsp;&nbsp;&nbsp;The percentile to use (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.25
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Percentile
  extends AbstractDoubleArrayRowStatistic {

  private static final long serialVersionUID = -4468456504069465058L;

  /** the percentile. */
  protected double m_Percentile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the specified percentile.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentile", "percentile",
      0.25, 0.0, 1.0);
  }

  /**
   * Sets the percentile to use.
   *
   * @param value	the percentile (0-1)
   */
  public void setPercentile(double value) {
    m_Percentile = value;
    reset();
  }

  /**
   * Returns the percentile in use.
   *
   * @return		the percentile (0-1)
   */
  public double getPercentile() {
    return m_Percentile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentileTipText() {
    return "The percentile to use (0-1).";
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
    SpreadSheet					result;
    Row 					row;
    adams.data.statistics.Percentile<Double> 	perc;
    int						i;

    result = createOutputHeader();

    perc = new adams.data.statistics.Percentile<>();
    for (i = 0; i < m_Values.size(); i++)
      perc.add(m_Values.get(i));

    row = result.addRow();
    row.addCell(0).setContent("Percentile (" + m_Percentile + ")");
    row.addCell(1).setContent(perc.getPercentile(m_Percentile));

    m_Values = null;

    return result;
  }
}
