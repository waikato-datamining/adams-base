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
 * FilteredOutlierDetector.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.data.filter.AbstractFilter;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A meta detector that first filters the data through a filter before pushing it through the base detector.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-filter &lt;gcms.data.filter.AbstractFilter [options]&gt; (property: filter)
 *         The filter to apply.
 *         default: gcms.data.filter.PassThrough
 * </pre>
 *
 * <pre>-detector &lt;gcms.data.outlier.AbstractOutlierDetector [options]&gt; (property: detector)
 *         The detector to use.
 *         default: gcms.data.outlier.PassThrough
 * </pre>
 *
 * Default options for gcms.data.filter.PassThrough (-filter/filter):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 * Default options for gcms.data.outlier.PassThrough (-detector/detector):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredOutlierDetector<T extends DataContainer>
  extends AbstractDatabaseConnectionOutlierDetector<T> {

  /** for serialization. */
  private static final long serialVersionUID = 6570555834680774914L;

  /** the filter to use. */
  protected AbstractFilter m_Filter;

  /** the detector to use. */
  protected AbstractOutlierDetector m_Detector;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A meta detector that first filters the data through a filter before "
      + "pushing it through the base detector.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Filter   = new adams.data.filter.PassThrough();
    m_Detector = new PassThrough();
  }

  /**
   * Resets the detector and filter (but does not clear the input data!).
   */
  @Override
  public void reset() {
    super.reset();

    if (m_Filter != null)
      m_Filter.reset();
    if (m_Detector != null)
      m_Detector.reset();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new adams.data.filter.PassThrough());

    m_OptionManager.add(
	    "detector", "detector",
	    new PassThrough());
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filterTipText() {
    return "The filter to apply.";
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter to use
   */
  public void setFilter(AbstractFilter value) {
    if (value != null) {
      m_Filter = value;
      updateDatabaseConnection();
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": filter cannot be null!");
    }
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public AbstractFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String detectorTipText() {
    return "The detector to use.";
  }

  /**
   * Sets the detector to use.
   *
   * @param value	the detector to use
   */
  public void setDetector(AbstractOutlierDetector value) {
    if (value != null) {
      m_Detector = value;
      updateDatabaseConnection();
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": detector cannot be null!");
    }
  }

  /**
   * Returns the detector in use.
   *
   * @return		the detector
   */
  public AbstractOutlierDetector getDetector() {
    return m_Detector;
  }

  /**
   * Updates the database connection in the outlier detectors.
   */
  @Override
  protected void updateDatabaseConnection() {
    if (m_Detector instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Detector).setDatabaseConnection(getDatabaseConnection());
    if (m_Filter instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Filter).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual detection: filters the data and then runs it through
   * the detector.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(DataContainer data) {
    List<String>	result;
    List<String>	detection;
    int			i;

    // filter data
    data = m_Filter.filter(data);
    m_Filter.cleanUp();

    // perform detection
    detection = m_Detector.detect(data);
    m_Detector.cleanUp();

    // add result to output
    result = new ArrayList<String>();
    for (i = 0; i < detection.size(); i++)
      result.add(new String(detection.get(i)));

    return result;
  }
}
