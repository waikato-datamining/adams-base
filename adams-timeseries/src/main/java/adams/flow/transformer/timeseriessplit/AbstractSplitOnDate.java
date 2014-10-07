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
 * AbstractSplitOnDate.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 * Ancestor for splitters that split on a date.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSplitOnDate
  extends AbstractTimeseriesSplitter {

  /** for serialization. */
  private static final long serialVersionUID = 259240444289354690L;

  /**
   * Determines what segments to return.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Segments {
    BOTH,
    BEFORE,
    AFTER
  }
  
  /** what segment to return. */
  protected Segments m_Segments;
  
  /** whether to include the split date in the segments (if exact hit). */
  protected boolean m_IncludeSplitDate;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "segments", "segments",
	    Segments.BOTH);

    m_OptionManager.add(
	    "include-split-date", "includeSplitDate",
	    false);
  }

  /**
   * Sets the segments to return.
   *
   * @param value	the segments
   */
  public void setSegments(Segments value) {
    m_Segments = value;
    reset();
  }

  /**
   * Returns the segments to return.
   *
   * @return		the segments
   */
  public Segments getSegments() {
    return m_Segments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String segmentsTipText() {
    return "The segments to return.";
  }

  /**
   * Sets whether to include the split date in the segments.
   *
   * @param value	ture if to include
   */
  public void setIncludeSplitDate(boolean value) {
    m_IncludeSplitDate = value;
    reset();
  }

  /**
   * Returns whether to include the split date in the segments.
   *
   * @return		true if to include
   */
  public boolean getIncludeSplitDate() {
    return m_IncludeSplitDate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String includeSplitDateTipText() {
    return "If enabled, the split date is included in the segments.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "segments", m_Segments, "segments: ");
    result += QuickInfoHelper.toString(this, "includeSplitDate", m_IncludeSplitDate, "incl split date", ", ");
    
    return result;
  }

  /**
   * Performs the actual split on the date.
   * 
   * @param series	the timeseries to split
   * @param date	the date to split on
   * @return		the generated sub-timeseries
   */
  protected Timeseries[] doSplit(Timeseries series, Date date) {
    List<Timeseries>	result;
    Timeseries		before;
    Timeseries		after;
    TimeseriesPoint	point;
    int			comp;
    
    result  = new ArrayList<Timeseries>();
    before  = series.getHeader();
    after   = series.getHeader();
    
    // split points
    for (Object obj: series.toList()) {
      point = (TimeseriesPoint) obj;
      comp  = point.getTimestamp().compareTo(date);
      if (comp == 0) {
	if (m_IncludeSplitDate) {
	  before.add(point);
	  after.add(point);
	}
      }
      else if (comp < 0) {
	before.add(point);
      }
      else {
	after.add(point);
      }
    }
    
    switch (m_Segments) {
      case BOTH:
	result.add(before);
	result.add(after);
	break;
      case BEFORE:
	result.add(before);
	break;
      case AFTER:
	result.add(after);
	break;
      default:
	throw new IllegalStateException("Unhandled segments: " + m_Segments);
    }
    
    return result.toArray(new Timeseries[result.size()]);
  }
}
