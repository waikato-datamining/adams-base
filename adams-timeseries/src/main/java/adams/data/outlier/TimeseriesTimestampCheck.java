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
 * TimestampCheck.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Index;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Checks whether the timestamp with the specified index meets the condition (eg before or after the supplied timestamp).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-index &lt;adams.core.Index&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the timestamp to use for the check.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-condition &lt;BEFORE|AFTER&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition that the selected timestamp must meet.
 * &nbsp;&nbsp;&nbsp;default: AFTER
 * </pre>
 * 
 * <pre>-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: timestamp)
 * &nbsp;&nbsp;&nbsp;The timestamp to use in the check.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 995 $
 */
public class TimeseriesTimestampCheck
  extends AbstractOutlierDetector<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -5300001549269138646L;

  /**
   * Determines what condition the selected timestamp must satisfy.
   * 
   * @author  FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 995 $
   */
  public enum TimestampCondition {
    /** timestamp must be before. */
    BEFORE,
    /** timestamp must be after. */
    AFTER,
  }
  
  /** the index of the timestamp to check. */
  protected Index m_Index;
  
  /** the condition that the timestamp must meet. */
  protected TimestampCondition m_Condition;
  
  /** the provided timestamp. */
  protected BaseDateTime m_Timestamp;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Checks whether the timestamp with the specified index meets the "
	+ "condition (eg before or after the supplied timestamp).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "index",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "condition", "condition",
	    TimestampCondition.AFTER);

    m_OptionManager.add(
	    "timestamp", "timestamp",
	    new BaseDateTime(BaseDateTime.INF_PAST));
  }

  /**
   * Sets the index of the timestamp to inspect.
   *
   * @param value	the index
   */
  public void setIndex(Index value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the timestamp to inspect.
   *
   * @return 		the index
   */
  public Index getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the timestamp to use for the check.";
  }

  /**
   * Sets the condition that the selected timestamp must meet.
   *
   * @param value	the condition
   */
  public void setCondition(TimestampCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the condition that the selected timestamp must meet.
   *
   * @return 		the condition
   */
  public TimestampCondition getCondition() {
    return m_Condition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String conditionTipText() {
    return "The condition that the selected timestamp must meet.";
  }

  /**
   * Sets the timestamp to use in the check.
   *
   * @param value	the timestamp
   */
  public void setTimestamp(BaseDateTime value) {
    m_Timestamp = value;
    reset();
  }

  /**
   * Returns the timestamp to use in the check.
   *
   * @return 		the timestamp
   */
  public BaseDateTime getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String timestampTipText() {
    return "The timestamp to use in the check.";
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to process
   */
  @Override
  protected void checkData(Timeseries data) {
    super.checkData(data);
    
    m_Index.setMax(data.size());
    if (m_Index.getIntIndex() == -1)
      throw new IllegalStateException("Invalid index: " + m_Index);
  }
  
  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(Timeseries data) {
    List<String>		result;
    String			msg;
    DateFormat			dformat;
    List<TimeseriesPoint>	points;
    TimeseriesPoint		point;
    Date			timestamp;
    
    result = new ArrayList<String>();
    
    dformat   = DateUtils.getTimestampFormatter();
    msg       = null;
    points    = data.toList();
    point     = points.get(m_Index.getIntIndex());
    timestamp = m_Timestamp.dateValue();
    
    switch (m_Condition) {
      case BEFORE:
	if (!DateUtils.isBefore(timestamp, point.getTimestamp()))
	  msg = "Timestamp at '" + m_Index + "' is not before supplied timestamp: " 
	      + dformat.format(point.getTimestamp()) + " > " + dformat.format(timestamp);
	break;
      case AFTER:
	if (!DateUtils.isAfter(timestamp, point.getTimestamp()))
	  msg = "Timestamp at '" + m_Index + "' is not after supplied timestamp: " 
	      + dformat.format(point.getTimestamp()) + " < " + dformat.format(timestamp);
	break;
      default:
	throw new IllegalStateException("Unhandled timestamp condition: " + m_Condition);
    }
    
    if (msg != null) {
      result.add(msg);
      if (isLoggingEnabled())
	getLogger().info(data.getDatabaseID() + " - " + getClass().getName() + ": " + msg);
    }

    return result;
  }
}
