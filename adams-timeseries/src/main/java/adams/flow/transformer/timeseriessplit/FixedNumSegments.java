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
 * FixedNumSegments.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.container.DataPoint;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Splits the timeseries into a fixed number of same-sized segements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-segments &lt;int&gt; (property: numSegments)
 * &nbsp;&nbsp;&nbsp;The number of segments to generate.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedNumSegments
  extends AbstractTimeseriesSplitter {

  /** for serialization. */
  private static final long serialVersionUID = 259240444289354690L;

  /** the number of segments. */
  protected int m_NumSegments;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the timeseries into a fixed number of same-sized segements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-segments", "numSegments",
	    2, 1, null);
  }

  /**
   * Sets the number of segments.
   *
   * @param value	the number of segments
   */
  public void setNumSegments(int value) {
    if (value >= 1) {
      m_NumSegments = value;
      reset();
    }
    else {
      getLogger().warning("Number of segments must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number segments.
   *
   * @return		the number of segments
   */
  public int getNumSegments() {
    return m_NumSegments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSegmentsTipText() {
    return "The number of segments to generate.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "numSegments", m_NumSegments, "# segments: ");
  }

  /**
   * Performs the actual split.
   * 
   * @param series	the timeseries to split
   * @return		the generated sub-timeseries
   */
  @Override
  protected Timeseries[] doSplit(Timeseries series) {
    List<Timeseries>	result;
    Timeseries		current;
    TimeseriesPoint	point;
    double		numPoints;
    double		max;
    int			i;
    int			n;
    int			offset;
    
    result    = new ArrayList<Timeseries>();
    current   = null;
    numPoints = series.size() / (double) m_NumSegments;
    max       = 0.0;
    offset    = 0;
    for (i = 0; i < m_NumSegments; i++) {
      max += numPoints;
      if (i == m_NumSegments - 1)
	max = series.size();
      current = series.getHeader();
      result.add(current);
      for (n = offset; n < max; n++) {
	point = (TimeseriesPoint) series.toList().get(n);
	current.add((DataPoint) point.getClone());
      }
      offset += current.size();
    }
    
    return result.toArray(new Timeseries[result.size()]);
  }
}
