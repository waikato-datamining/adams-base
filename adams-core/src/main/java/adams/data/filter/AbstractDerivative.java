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
 * AbstractDerivative.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 * Abstract ancestor for Derivative filters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to filter
 */
public abstract class AbstractDerivative<T extends DataContainer>
  extends AbstractFilter<T> {

  /**
   * Container class for abundance and timestamp in double format. In order
   * to avoid rounding after every derivation step.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Point
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -5500693386976351214L;

    /** the X value. */
    protected double m_X;

    /** the Y value. */
    protected double m_Y;

    /**
     * Initializes the point.
     *
     * @param x		the x as double
     * @param y		the y as double
     */
    public Point(double x, double y) {
      super();

      m_X = x;
      m_Y = y;
    }

    /**
     * Returns the X value.
     *
     * @return		the X value
     */
    public double getX() {
      return m_X;
    }

    /**
     * Returns the Y value.
     *
     * @return		the Y value
     */
    public double getY() {
      return m_Y;
    }

    /**
     * Sets the Y value.
     *
     * @param value	the Y value
     */
    public void setY(double value) {
      m_Y = value;
    }

    /**
     * Returns the point as string.
     *
     * @return			a string representation
     */
    @Override
    public String toString() {
      return "X=" + m_X + ", Y=" + m_Y;
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = 530300053103127948L;

  /** the order of the derivative. */
  protected int m_Order;

  /** the range to scale the abundances to after each derivation step. */
  protected double m_ScalingRange;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "order", "order",
	    1);

    m_OptionManager.add(
	    "scaling", "scalingRange", 0.0);
  }

  /**
   * Sets the order of the derivative to calculate.
   *
   * @param value	the order
   */
  public void setOrder(int value) {
    if (value > 0) {
      m_Order = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": only positive numbers are allowed!");
    }
  }

  /**
   * Returns the order of the derivative to calculate.
   *
   * @return		the order
   */
  public int getOrder() {
    return m_Order;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String orderTipText() {
    return "The order of the derivative to calculate.";
  }

  /**
   * Sets the range to scale the abundances to after each derivation step
   * (= 0 turns scaling off; -1 sets scaling to input range).
   *
   * @param value	the range
   */
  public void setScalingRange(double value) {
    m_ScalingRange = value;
    reset();
  }

  /**
   * Returns the scaling range the abundances are scaled to after each
   * derivation step (= 0 means no scaling; -1 sets scaling to input range).
   *
   * @return		the range
   */
  public double getScalingRange() {
    return m_ScalingRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String scalingRangeTipText() {
    return
        "The range to scale the abundances to after each derivation step; "
      + "use 0 to turn off and -1 to set it to the input range.";
  }

  /**
   * Generates the next derivative from the GC data.
   *
   * @param data	the data to derive
   * @return		the derived data
   */
  protected List<Point> derive(List<Point> data) {
    List<Point>	result;
    Point		newPoint;
    Point		currPoint;
    Point		prevPoint;
    int			i;
    double		minAbund;
    double		maxAbund;
    double		minAbundInput;
    double		maxAbundInput;
    double		range;
    double		factor;
    boolean		scaling;

    result        = new ArrayList<Point>();

    scaling       = ((m_ScalingRange > 0) || (m_ScalingRange == -1));
    minAbund      = Double.MAX_VALUE;
    maxAbund      = -Double.MAX_VALUE;
    minAbundInput = Double.MAX_VALUE;
    maxAbundInput = -Double.MAX_VALUE;

    for (i = 1; i < data.size(); i++) {
      prevPoint = data.get(i - 1);
      currPoint = data.get(i);
      newPoint  = new Point(
	  		prevPoint.getX(),
	  		    (currPoint.getY() - prevPoint.getY()) /
	  		    (currPoint.getX() - prevPoint.getX()));

      // determine range
      if (scaling) {
	if (prevPoint.getY() > maxAbundInput)
	  maxAbundInput = prevPoint.getY();
	if (prevPoint.getY() < minAbundInput)
	  minAbundInput = prevPoint.getY();

	if (newPoint.getY() > maxAbund)
	  maxAbund = newPoint.getY();
	if (newPoint.getY() < minAbund)
	  minAbund = newPoint.getY();
      }

      result.add(newPoint);
    }

    // scale?
    if (scaling) {
      range = maxAbund - minAbund;
      if (range != 0) {
	// original input scale?
	if(m_ScalingRange == -1)
	  factor = (maxAbundInput - minAbundInput) / range;
	else
	  factor = m_ScalingRange / range;

	for (i = 0; i < result.size(); i++)
	  result.get(i).setY(result.get(i).getY() * factor);
      }
    }

    return result;
  }

  /**
   * Turns the DataPoint into the intermediate format.
   *
   * @param point	the DataPoint to convert
   * @return		the generated intermediate format point
   */
  protected abstract Point toPoint(DataPoint point);

  /**
   * Turns the intermediate format point back into a DataPoint.
   *
   * @param point	the intermediate format point to convert
   * @return		the generated DataPoint
   */
  protected abstract DataPoint toDataPoint(Point point);

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T			result;
    int			i;
    List<DataPoint>	datapoints;
    List<Point>		points;

    // transform data to doubles
    datapoints = data.toList();
    points     = new ArrayList<Point>();
    for (i = 0; i < datapoints.size(); i++)
      points.add(toPoint(datapoints.get(i)));

    // perform derivations
    for (i = 0; i < m_Order; i++)
      points = derive(points);

    // transform data back into chromatogram
    result = (T) data.getHeader();
    for (i = 0; i < points.size(); i++)
      result.add(toDataPoint(points.get(i)));

    return result;
  }
}
