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
 * TimeseriesWindow.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.core.DateUtils;
import adams.core.base.BaseDateTime;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Leaves only the specified window in the timeseries (borders included).<br>
 * The matching can be inverted, i.e., everything but the window is returned.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-start &lt;adams.core.base.BaseDateTime&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The timestamp for the first data point in series to keep.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 * <pre>-end &lt;adams.core.base.BaseDateTime&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;The timestamp for the last data point in series to keep.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 * 
 * <pre>-invert &lt;boolean&gt; (property: invert)
 * &nbsp;&nbsp;&nbsp;If enabled, everything but the window is kept.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesWindow
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;
  
  /** the starting point. */
  protected BaseDateTime m_Start;
  
  /** the end point. */
  protected BaseDateTime m_End;
  
  /** whether to invert the matching. */
  protected boolean m_Invert;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Leaves only the specified window in the timeseries (borders included).\n"
	+ "The matching can be inverted, i.e., everything but the window is returned.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "end", "end",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the start timestamp for series.
   *
   * @param value	the timestamp
   */
  public void setStart(BaseDateTime value) {
    m_Start = value;
    reset();
  }

  /**
   * The start timestamp of series.
   *
   * @return		the timestamp
   */
  public BaseDateTime getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The timestamp for the first data point in series to keep.";
  }

  /**
   * Sets the end timestamp for series.
   *
   * @param value	the timestamp
   */
  public void setEnd(BaseDateTime value) {
    m_End = value;
    reset();
  }

  /**
   * The end timestamp of series.
   *
   * @return		the timestamp
   */
  public BaseDateTime getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The timestamp for the last data point in series to keep.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether the matching is inverted.
   *
   * @return		true if inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, everything but the window is kept.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries		result;
    TimeseriesPoint	point;
    int			i;
    Date		start;
    Date		end;

    result = data.getHeader();
    start  = m_Start.dateValue();
    end    = m_End.dateValue();
    for (i = 0; i < data.size(); i++) {
      point = (TimeseriesPoint) data.toList().get(i);
      if (m_Invert) {
	if (!(DateUtils.isBefore(start, point.getTimestamp()) || DateUtils.isAfter(end, point.getTimestamp())))
	  continue;
      }
      else {
	if (DateUtils.isBefore(start, point.getTimestamp()) || DateUtils.isAfter(end, point.getTimestamp()))
	  continue;
      }
      result.add(new TimeseriesPoint(point.getTimestamp(), point.getValue()));
    }
    
    return result;
  }
}
