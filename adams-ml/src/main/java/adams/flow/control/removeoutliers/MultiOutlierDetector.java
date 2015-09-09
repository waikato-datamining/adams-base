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
 * MultiOutlierDetector.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.removeoutliers;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Applies the specified outlier detectors sequentially and combines the detected outliers either via union or intersect.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-detector &lt;adams.flow.control.removeoutliers.AbstractOutlierDetector&gt; [-detector ...] (property: detectors)
 * &nbsp;&nbsp;&nbsp;The detectors to apply sequentially.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-combination &lt;UNION|INTERSECT&gt; (property: combination)
 * &nbsp;&nbsp;&nbsp;How to combine the outliers from the various detectors.
 * &nbsp;&nbsp;&nbsp;default: UNION
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiOutlierDetector
  extends AbstractOutlierDetector {

  private static final long serialVersionUID = 6451004929042775852L;

  /**
   * Enumeration of how to combine the outliers.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Combination {
    UNION,
    INTERSECT
  }

  /** outlier detectors to apply. */
  protected AbstractOutlierDetector[] m_Detectors;

  /** how to combine the outliers. */
  protected Combination m_Combination;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified outlier detectors sequentially and combines the detected outliers either via union or intersect.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "detector", "detectors",
      new AbstractOutlierDetector[0]);

    m_OptionManager.add(
      "combination", "combination",
      Combination.UNION);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "combination", m_Combination);
  }

  /**
   * Sets the detectors to use.
   *
   * @param value	the detectors
   */
  public void setDetectors(AbstractOutlierDetector[] value) {
    m_Detectors = value;
    reset();
  }

  /**
   * Returns the detectors to use.
   *
   * @return		the detectors
   */
  public AbstractOutlierDetector[] getDetectors() {
    return m_Detectors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String detectorsTipText() {
    return "The detectors to apply sequentially.";
  }

  /**
   * Sets how to combine the outliers.
   *
   * @param value	the combination
   */
  public void setCombination(Combination value) {
    m_Combination = value;
    reset();
  }

  /**
   * Returns how to combine the outliers.
   *
   * @return		the combination
   */
  public Combination getCombination() {
    return m_Combination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String combinationTipText() {
    return "How to combine the outliers from the various detectors.";
  }

  /**
   * Check method before detection.
   *
   * @param sheet	the spreadsheet to analyze
   * @param actual	the column with the actual values
   * @param predicted	the column with the predicted values
   * @return		null if check passed, otherwise error message
   */
  @Override
  public String check(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted) {
    String	result;
    int		i;

    result = super.check(sheet, actual, predicted);

    if (result == null) {
      for (i = 0; i < m_Detectors.length; i++) {
	result = m_Detectors[i].check(sheet, actual, predicted);
	if (result != null) {
	  result = "Detector #" + (i+1) + ": " + result;
	  break;
	}
      }
    }

    return result;
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
    Set<Integer>	current;
    int			i;

    result = new HashSet<Integer>();
    for (i = 0; i < m_Detectors.length; i++) {
      current = m_Detectors[i].detect(sheet, actual, predicted);
      if (i == 0)
	result = current;
      else {
	switch (m_Combination) {
	  case INTERSECT:
	    result.containsAll(current);
	    break;
	  case UNION:
	    result.addAll(current);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled combination: " + m_Combination);
	}
      }
    }

    return result;
  }
}
