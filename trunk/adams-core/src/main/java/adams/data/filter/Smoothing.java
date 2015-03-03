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
 * Smoothing.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataContainer;
import adams.data.smoothing.AbstractSmoother;
import adams.data.smoothing.PassThrough;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A filter that runs a smoothing scheme over the data.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-smoother &lt;gcms.data.smoothing.AbstractSmoother [options]&gt; (property: smoother)
 *         The smoothing algorithm to use for smoothing the GC points (abundances)
 *         of the chromatogram.
 *         default: gcms.data.smoothing.SlidingWindow -window 20 -measure MEDIAN
 * </pre>
 *
 * Default options for gcms.data.smoothing.SlidingWindow (-smoother/smoother):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-window &lt;int&gt; (property: windowSize)
 *         The window size for determining the 'smoothed' abundances.
 *         default: 20
 * </pre>
 *
 * <pre>-measure &lt;MEDIAN|MEAN&gt; (property: measure)
 *         The measure to use for calculating the 'smoothed' abundances.
 *         default: MEDIAN
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$\
 * @param <T> the type of data to smooth
 */
public class Smoothing<T extends DataContainer>
  extends AbstractDatabaseConnectionFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -3912578009638673851L;

  /** the smoothing scheme. */
  protected AbstractSmoother m_Smoother;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A filter that runs a smoothing scheme over the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "smoother", "smoother",
	    new PassThrough());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the smoothing algorithm.
   *
   * @param value 	the algorithm
   */
  public void setSmoother(AbstractSmoother value) {
    m_Smoother = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the current smoothing algorithm.
   *
   * @return 		the algorithm
   */
  public AbstractSmoother getSmoother() {
    return m_Smoother;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String smootherTipText() {
    return
        "The smoothing algorithm to use for smoothing the GC points (abundances) "
      + "of the chromatogram.";
  }

  /**
   * Updates the database connection in the smoother.
   */
  protected void updateDatabaseConnection() {
    if (m_Smoother instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Smoother).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    T			result;
    AbstractSmoother	smoother;

    smoother = m_Smoother.shallowCopy(true);
    result   = (T) smoother.smooth(data);
    smoother.destroy();

    return result;
  }
}
