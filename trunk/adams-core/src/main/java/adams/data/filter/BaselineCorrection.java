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
 * BaselineCorrection.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.baseline.AbstractBaselineCorrection;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A filter that runs a baseline correction scheme over the data.
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public class BaselineCorrection<T extends DataContainer>
  extends AbstractDatabaseConnectionFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -7883650579561992382L;

  /** the baseline correction algorithm. */
  protected AbstractBaselineCorrection m_BaselineCorrection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A filter that runs a baseline correction scheme over the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "baseline", "baselineCorrection",
	    new adams.data.baseline.PassThrough());
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
   * Updates the database connection in the baseline correction.
   */
  protected void updateDatabaseConnection() {
    if (m_BaselineCorrection instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_BaselineCorrection).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    T				result;
    AbstractBaselineCorrection	baseline;

    baseline = m_BaselineCorrection.shallowCopy(true);
    result   = (T) baseline.correct((T) data);
    baseline.destroy();

    return result;
  }
}
