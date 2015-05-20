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
 * DownSize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 <!-- globalinfo-start -->
 * A filter that picks a specified number of evenly spaced data points from the data. Does not introduce extra data points if there are too few points.
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
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of points to pick from the input data.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1286 $
 */
public class DownSize<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -7633117391523711914L;

  /** the number of points to retain. */
  protected int m_NumPoints;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"A filter that picks a specified number of evenly spaced data "
	+ "points from the data. Does not introduce extra data points if "
	+ "there are too few points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    getDefaultNumPoints(), 1, null);
  }

  /**
   * Returns the default number of points to retain.
   * 
   * @return		the default
   */
  protected int getDefaultNumPoints() {
    return 100;
  }
  
  /**
   * Sets the number of points to retain.
   *
   * @param value 	the number
   */
  public void setNumPoints(int value) {
    m_NumPoints = value;
    reset();
  }

  /**
   * Returns the number of points to retain.
   *
   * @return 		the number
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
    return "The number of points to pick from the input data.";
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
    int		i;
    int		index;
    
    List<DataPoint>	points;

    // no subset?
    if (data.size() <= m_NumPoints)
      return (T) data.getClone();
    
    points = data.toList();
    result = (T) data.getHeader();
    
    for (i = 0; i < m_NumPoints; i++) {
      index = (int) Math.round(i * ((double) data.size() / (double) m_NumPoints));
      result.add((DataPoint) points.get(index).getClone());
    }

    return result;
  }
}
