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
 * MeanAbsoluteError.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.removeoutliers;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.statistics.StatUtils;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Mean absolute error (MAE) based detector.<br>
 * If difference between actual&#47;predicted is more than MAE * FACTOR, then the point gets flagged as outlier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-factor &lt;double&gt; (property: factor)
 * &nbsp;&nbsp;&nbsp;The factor which determines whether a value is an outlier.
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MeanAbsoluteError
  extends AbstractNumericOutlierDetector {

  private static final long serialVersionUID = 6451004929042775852L;

  /** the stdev factor. */
  protected double m_Factor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Mean absolute error (MAE) based detector.\n"
	+ "If difference between actual/predicted is more than MAE * FACTOR, "
        + "then the point gets flagged as outlier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "factor", "factor",
      2.0, 0.0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "factor", m_Factor, "factor: ");
  }

  /**
   * Sets the factor which determines whether a value is an outlier.
   *
   * @param value	the factor
   */
  public void setFactor(double value) {
    m_Factor = value;
    reset();
  }

  /**
   * Returns the factor which determines whether a value is an outlier.
   *
   * @return		the factor
   */
  public double getFactor() {
    return m_Factor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String factorTipText() {
    return "The factor which determines whether a value is an outlier.";
  }

  /**
   * Performs the actual detection of the outliers.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		the row indices of the outliers
   */
  @Override
  protected Set<Integer> doDetect(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted) {
    Set<Integer>	result;
    Double[]		act;
    Double[]		pred;
    double[] 		diff;
    double 		mae;
    int			i;

    result = new HashSet<>();

    // calc differences
    act  = extractColumn(sheet, actual);
    pred = extractColumn(sheet, predicted);
    diff = diff(act, pred, false);

    // stats
    mae = StatUtils.mae(act, pred);
    if (isLoggingEnabled())
      getLogger().info("mae=" + mae);

    // flag outliers
    for (i = 0; i < diff.length; i++) {
      if (Math.abs(diff[i]) > mae * m_Factor)
	result.add(i);
    }

    return result;
  }
}
