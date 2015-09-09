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
 * IQR.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
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
 * Interquartile range based detector.<br>
 * If difference between actual&#47;predicted is more than the factor of standard deviations away from the quartial 0.25&#47;0.75, then the point gets flagged as outlier.
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
 * &nbsp;&nbsp;&nbsp;default: 3.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-use-relative &lt;boolean&gt; (property: useRelative)
 * &nbsp;&nbsp;&nbsp;If enabled, relative values (divided by actual) are used instead of absolute 
 * &nbsp;&nbsp;&nbsp;ones.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IQR
  extends AbstractNumericOutlierDetector {

  private static final long serialVersionUID = 6451004929042775852L;

  /** the outlier factor. */
  protected double m_Factor;

  /** whether to use relative values instead of absolute ones. */
  protected boolean m_UseRelative;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Interquartile range based detector.\n"
	+ "If difference between actual/predicted is more than the factor of "
	+ "standard deviations away from the quartial 0.25/0.75, then the point "
	+ "gets flagged as outlier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "factor", "factor",
      3.0, 0.0, null);

    m_OptionManager.add(
      "use-relative", "useRelative",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "factor", m_Factor, "factor: ");
    value  = QuickInfoHelper.toString(this, "useRelative", m_UseRelative, "relative", ", ");
    if (value != null)
      result += value;

    return result;
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
   * Sets whether to use relative values (divided by actual) rather than
   * absolute ones.
   *
   * @param value	true if relative
   */
  public void setUseRelative(boolean value) {
    m_UseRelative = value;
    reset();
  }

  /**
   * Returns whether to use relative values (divided by actual) rather than
   * absolute ones.
   *
   * @return		true if relative
   */
  public boolean getUseRelative() {
    return m_UseRelative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRelativeTipText() {
    return "If enabled, relative values (divided by actual) are used instead of absolute ones.";
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
    double		iqr1;
    double		iqr3;
    double		iqr;
    int			i;

    result = new HashSet<>();

    // calc differences
    diff = new double[sheet.getRowCount()];
    act  = extractColumn(sheet, actual);
    pred = extractColumn(sheet, predicted);
    for (i = 0; i < sheet.getRowCount(); i++) {
      if ((act[i] != null) && (pred[i] != null)) {
	diff[i] = act[i] - pred[i];
	if (m_UseRelative) {
	  if (act[i] != 0)
	    diff[i] /= act[i];
	  else
	    diff[i] = Double.NaN;
	}
      }
      else {
	diff[i] = Double.NaN;
      }
    }

    // stats
    iqr1 = StatUtils.quartile(diff, 0.25);
    iqr3 = StatUtils.quartile(diff, 0.75);
    iqr  = iqr3 - iqr1;
    if (isLoggingEnabled())
      getLogger().info("iqr1=" + iqr1 + ", iqr3=" + iqr3 + ", iqr=" + iqr);

    // flag outliers
    for (i = 0; i < diff.length; i++) {
      if (diff[i] > iqr3 + iqr * m_Factor)
	result.add(i);
      else if (diff[i] < iqr1 - iqr * m_Factor)
	result.add(i);
    }

    return result;
  }
}
