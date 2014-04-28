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
 * AbstractSlidingWindow.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.statistics.StatUtils;

/**
 * Abstract sliding window smoother.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractSlidingWindow<T extends DataContainer>
  extends AbstractSmoother<T> {

  /**
   * The type of measure to use for computing the "smoothed" points in the
   * sliding window.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Measure {
    /** uses the median of the sliding window. */
    MEDIAN,
    /** uses the mean of the sliding window. */
    MEAN
  }

  /** for serialization. */
  private static final long serialVersionUID = 4528493242259502282L;

  /** the window size. */
  protected int m_WindowSize;

  /** the type of measure to use. */
  protected Measure m_Measure;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "window", "windowSize",
	    20);

    m_OptionManager.add(
	    "measure", "measure",
	    Measure.MEDIAN);
  }

  /**
   * Sets the window size for determining the 'smoothed' abundances.
   *
   * @param value	the window size
   */
  public void setWindowSize(int value) {
    if (value > 0) {
      m_WindowSize = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ".windowSize: only positive numbers are allowed for window size!");
    }
  }

  /**
   * Returns the window size for determining the 'smoothed' abundances.
   *
   * @return		the window size
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String windowSizeTipText() {
    return "The window size for determining the 'smoothed' abundances.";
  }

  /**
   * Sets the measure to use.
   *
   * @param value	the measure
   */
  public void setMeasure(Measure value) {
    m_Measure = value;
    reset();
  }

  /**
   * Returns the current measure in use.
   *
   * @return		the measure
   */
  public Measure getMeasure() {
    return m_Measure;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String measureTipText() {
    return "The measure to use for calculating the 'smoothed' abundances.";
  }

  /**
   * Returns the X-value of the data point.
   *
   * @param point	the point to get the X-value from
   * @return		the X-value
   */
  protected abstract Double getValue(DataPoint point);

  /**
   * Updates the X-value of the data point.
   *
   * @param point	the point to update
   * @param value	the value to update the point with
   */
  protected abstract void updatePoint(DataPoint point, double value);

  /**
   * Performs the actual smoothing.
   *
   * @param data	the to smooth
   * @return		the smoothed data
   */
  @Override
  protected T processData(T data) {
    T			result;
    Double[]		abundances;
    double		measure;
    int			i;
    int			n;
    int			pos;
    int			left;
    int			right;
    List<DataPoint>	points;
    DataPoint		point;

    result     = (T) data.getHeader();
    points     = data.toList();
    abundances = new Double[m_WindowSize];

    // the number of dummy points to add, left and right
    pos   = m_WindowSize / 2;
    left  = pos;
    right = m_WindowSize - left;

    for (i = 0; i < points.size() - m_WindowSize + 1; i++) {
      for (n = 0; n < m_WindowSize; n++)
	abundances[n] = getValue(points.get(i + n));
      if (m_Measure == Measure.MEDIAN)
	measure = (float) StatUtils.median(abundances);
      else if (m_Measure == Measure.MEAN)
	measure = (float) StatUtils.mean(abundances);
      else
	throw new IllegalStateException("Unhandled measure: " + m_Measure);

      // add smoothed point, i.e., median
      point = (DataPoint) points.get(i + pos).getClone();
      updatePoint(point, measure);
      result.add(point);
    }

    // add dummy points on the left side
    measure = getValue((DataPoint) result.toList().get(0));
    for (n = 0; n < left; n++) {
      point = (DataPoint) points.get(n).getClone();
      updatePoint(point, measure);
      result.add(point);
    }

    // add dummy points on the right side
    measure = getValue((DataPoint) result.toList().get(result.toList().size() - 1));
    for (n = 0; n < right; n++) {
      point = (DataPoint) points.get(points.size() - m_WindowSize + pos + n).getClone();
      updatePoint(point, measure);
      result.add(point);
    }

    return result;
  }
}
