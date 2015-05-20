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
 * FixedDate.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;

/**
 <!-- globalinfo-start -->
 * Splits the timeseries using the specified date.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-segments &lt;BOTH|BEFORE|AFTER&gt; (property: segments)
 * &nbsp;&nbsp;&nbsp;The segments to return.
 * &nbsp;&nbsp;&nbsp;default: BOTH
 * </pre>
 * 
 * <pre>-include-split-date &lt;boolean&gt; (property: includeSplitDate)
 * &nbsp;&nbsp;&nbsp;If enabled, the split date is included in the segments.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-date &lt;adams.core.base.BaseDateTime&gt; (property: date)
 * &nbsp;&nbsp;&nbsp;The split date to use.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedDate
  extends AbstractSplitOnDate {

  /** for serialization. */
  private static final long serialVersionUID = 259240444289354690L;
  
  /** the date to split on. */
  protected BaseDateTime m_Date;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Splits the timeseries using the specified date.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "date", "date",
	    new BaseDateTime(BaseDateTime.NOW));
  }

  /**
   * Sets the split date.
   *
   * @param value	the date
   */
  public void setDate(BaseDateTime value) {
    m_Date = value;
    reset();
  }

  /**
   * Returns the split date.
   *
   * @return		the date
   */
  public BaseDateTime getDate() {
    return m_Date;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateTipText() {
    return "The split date to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "date", m_Date, ", date: ");
    
    return result;
  }

  /**
   * Performs the actual split.
   * 
   * @param series	the timeseries to split
   * @return		the generated sub-timeseries
   */
  @Override
  protected Timeseries[] doSplit(Timeseries series) {
    return doSplit(series, m_Date.dateValue());
  }
}
