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
 * FixedLengthSegments.java
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
 * Splits the timeseries into segements of a fixed length, i.e., number of data points.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of data points for a segment.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 0
 * </pre>
 * 
 * <pre>-discard-partial &lt;boolean&gt; (property: discardPartial)
 * &nbsp;&nbsp;&nbsp;If enabled, partial segments get discarded, ie the last one if not sufficient 
 * &nbsp;&nbsp;&nbsp;data points.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedLengthSegments
  extends AbstractTimeseriesSplitter {

  /** for serialization. */
  private static final long serialVersionUID = 259240444289354690L;

  /** the number of data points. */
  protected int m_NumPoints;
  
  /** whether to discard partial segments (ie the last one). */
  protected boolean m_DiscardPartial;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the timeseries into segements of a fixed length, i.e., number of data points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    100, 1, null);

    m_OptionManager.add(
	    "discard-partial", "discardPartial",
	    false);
  }

  /**
   * Sets the number of points for a segment.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    if (value >= 1) {
      m_NumPoints = value;
      reset();
    }
    else {
      getLogger().warning("Number of data points must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the number of points for a segment.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of data points for a segment.";
  }

  /**
   * Sets whether to discard partial segments.
   *
   * @param value	ture if to discard
   */
  public void setDiscardPartial(boolean value) {
    m_DiscardPartial = value;
    reset();
  }

  /**
   * Returns whether to discard partial segments.
   *
   * @return		true if to discard
   */
  public boolean getDiscardPartial() {
    return m_DiscardPartial;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String discardPartialTipText() {
    return "If enabled, partial segments get discarded, ie the last one if not sufficient data points.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "numPoints", m_NumPoints, "# points: ");
    result += QuickInfoHelper.toString(this, "discardPartial", m_DiscardPartial, "no partial", ", ");
    
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
    List<Timeseries>	result;
    Timeseries		current;
    TimeseriesPoint	point;
    
    result  = new ArrayList<Timeseries>();
    current = null;
    for (Object p: series.toList()) {
      point = (TimeseriesPoint) p;
      if (current == null) {
	current = series.getHeader();
	result.add(current);
      }
      if (current.size() < m_NumPoints)
	current.add((DataPoint) point.getClone());
      if (current.size() == m_NumPoints)
	current = null;
    }
    
    // discard?
    if (m_DiscardPartial && (result.size() > 0)) {
      if (result.get(result.size() - 1).size() < m_NumPoints)
	result.remove(result.size() - 1);
    }
    
    return result.toArray(new Timeseries[result.size()]);
  }
}
