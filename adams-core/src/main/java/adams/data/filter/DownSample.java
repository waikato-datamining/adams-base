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
 * DownSample.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 <!-- globalinfo-start -->
 * A filter that returns only every n-th data point.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
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
 * <pre>-nth &lt;int&gt; (property: nthPoint)
 * &nbsp;&nbsp;&nbsp;Only every n-th point will be output.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class DownSample<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -7633117391523711914L;

  /** the n-th point to use. */
  protected int m_NthPoint;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A filter that returns only every n-th data point.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "nth", "nthPoint",
	    1);
  }

  /**
   * Sets the nth point setting.
   *
   * @param value 	the nth point to use
   */
  public void setNthPoint(int value) {
    m_NthPoint = value;
    reset();
  }

  /**
   * Returns the nth point setting.
   *
   * @return 		the nth point
   */
  public int getNthPoint() {
    return m_NthPoint;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthPointTipText() {
    return "Only every n-th point will be output.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T		result;
    int			i;
    List<DataPoint>	points;

    result = (T) data.getHeader();

    points = data.toList();
    for (i = 0; i < points.size(); i++) {
      if ((i+1) % m_NthPoint == 0)
	result.add((DataPoint) points.get(i).getClone());
    }

    return result;
  }
}
