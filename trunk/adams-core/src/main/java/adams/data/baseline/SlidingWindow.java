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
 * Window.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.baseline;

import java.util.List;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * Applies a baseline correction scheme on partitions of the data with a sliding window approach.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-num-left &lt;int&gt; (property: numLeft)
 * &nbsp;&nbsp;&nbsp;The number of points left of the center point.
 * &nbsp;&nbsp;&nbsp;default: 30
 * </pre>
 *
 * <pre>-num-right &lt;int&gt; (property: numRight)
 * &nbsp;&nbsp;&nbsp;The number of points right of the center point.
 * &nbsp;&nbsp;&nbsp;default: 30
 * </pre>
 *
 * <pre>-baseline &lt;adams.data.baseline.AbstractBaselineCorrection [options]&gt; (property: baselineCorrection)
 * &nbsp;&nbsp;&nbsp;The baseline correction algorithm to use for correcting the baseline of
 * &nbsp;&nbsp;&nbsp;the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.baseline.PassThrough
 * </pre>
 *
 * Default options for adams.data.baseline.PassThrough (-baseline/baselineCorrection):
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public class SlidingWindow<T extends DataContainer>
  extends AbstractDatabaseConnectionBaselineCorrection<T> {

  /** for serialization. */
  private static final long serialVersionUID = -3975367203680893657L;

  /** the number of points left of the center. */
  protected int m_NumLeft;

  /** the number of points right of the center. */
  protected int m_NumRight;

  /** the actual baseline correction scheme to apply. */
  protected AbstractBaselineCorrection m_BaselineCorrection;

  /**
   * Returns a string describing the object.
   *
   * @return         a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies a baseline correction scheme on partitions of the data with "
      + "a sliding window approach.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-left", "numLeft",
	    30);

    m_OptionManager.add(
	    "num-right", "numRight",
	    30);

    m_OptionManager.add(
	    "baseline", "baselineCorrection",
	    new adams.data.baseline.PassThrough());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the number of points left of the center.
   *
   * @param value 	the number of points
   */
  public void setNumLeft(int value) {
    if (value > 0) {
      m_NumLeft = value;
      reset();
    }
    else {
      getLogger().severe("At least one point is required!");
    }
  }

  /**
   * Returns the number of points left of the center.
   *
   * @return 		the number of points
   */
  public int getNumLeft() {
    return m_NumLeft;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numLeftTipText() {
    return "The number of points left of the center point.";
  }

  /**
   * Sets the number of points right of the center.
   *
   * @param value 	the number of points
   */
  public void setNumRight(int value) {
    if (value > 0) {
      m_NumRight = value;
      reset();
    }
    else {
      getLogger().severe("At least one point is required!");
    }
  }

  /**
   * Returns the number of points right of the center.
   *
   * @return 		the number of points
   */
  public int getNumRight() {
    return m_NumRight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRightTipText() {
    return "The number of points right of the center point.";
  }

  /**
   * Sets the baseline correction algorithm.
   *
   * @param value 	the algorithm
   */
  public void setBaselineCorrection(AbstractBaselineCorrection value) {
    m_BaselineCorrection = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the current baseline correction algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractBaselineCorrection getBaselineCorrection() {
    return m_BaselineCorrection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String baselineCorrectionTipText() {
    return
        "The baseline correction algorithm to use for correcting the baseline "
      + "of the data.";
  }

  /**
   * Updates the database connection in dependent schemes.
   */
  @Override
  protected void updateDatabaseConnection() {
    if (m_BaselineCorrection instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_BaselineCorrection).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual correcting.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  @Override
  protected T processData(T data) {
    T				result;
    AbstractBaselineCorrection	correction;
    int				i;
    int				n;
    int				size;
    int				left;
    int				right;
    List<DataPoint>		points;
    T				window;
    T				windowCorr;

    result = (T) data.getHeader();

    correction = m_BaselineCorrection.shallowCopy(true);
    points     = data.toList();
    window     = (T) data.getHeader();
    for (i = 0; i < points.size(); i++) {
      // determine points left and right
      left = i;
      if (left > m_NumLeft)
	left = m_NumLeft;
      right = points.size() - i - 1;
      if (right > m_NumRight)
	right = m_NumRight;
      size = left + 1 + right;

      // create window
      window.clear();
      for (n = 0; n < size; n++)
	window.add(points.get(i - left + n).getClone());

      // apply baseline correction
      windowCorr = (T) correction.correct(window);
      if (windowCorr.size() != size)
	throw new IllegalStateException(
	    "Baseline correction scheme returned different number of points (expected/actual): "
	    + size + " != " + windowCorr.size());

      // obtain central point
      result.add(((DataPoint) windowCorr.toList().get(left)).getClone());
    }
    correction.cleanUp();

    return result;
  }
}
