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
 * MultiOutlierDetector.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A meta-detector that runs multiple outlier detectors over the data.
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
 * <pre>-detector &lt;gcms.data.outlier.AbstractOutlierDetector [options]&gt; [-detector ...] (property: subDetectors)
 *         The array of outlier detectors to use.
 * </pre>
 *
 * <pre>-stop (property: stopOnFirstDetection)
 *         If set to true, the detection process will be stopped as soon as one of
 *         the sub-detecors detected an outlier.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to pass through the detector
 */
public class MultiOutlierDetector<T extends DataContainer>
  extends AbstractDatabaseConnectionOutlierDetector<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5818338370944478215L;

  /** the detectors. */
  protected AbstractOutlierDetector[] m_Detectors;

  /** stops detection as soon as one sub-detector detected something. */
  protected boolean m_StopOnFirstDetection;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A meta-detector that runs multiple outlier detectors over the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "detector", "subDetectors",
	    new AbstractOutlierDetector[]{new PassThrough()});

    m_OptionManager.add(
	    "stop", "stopOnFirstDetection",
	    false);
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
  public String subDetectorsTipText() {
    return "The array of outlier detectors to use.";
  }

  /**
   * Sets the detectors to use.
   *
   * @param value	the detectors to use
   */
  public void setSubDetectors(AbstractOutlierDetector[] value) {
    if (value != null) {
      m_Detectors = value;
      updateDatabaseConnection();
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": detectors cannot be null!");
    }
  }

  /**
   * Returns the detectors in use.
   *
   * @return		the detectors
   */
  public AbstractOutlierDetector[] getSubDetectors() {
    return m_Detectors;
  }

  /**
   * Sets whether detection process is stopped as soon as one detector
   * detected something.
   *
   * @param value 	true if detection is to be stopped
   */
  public void setStopOnFirstDetection(boolean value) {
    m_StopOnFirstDetection = value;
    reset();
  }

  /**
   * Returns whether detection process is stopped as soon as one detector
   * detected something.
   *
   * @return 		true if detection is stopped
   */
  public boolean getStopOnFirstDetection() {
    return m_StopOnFirstDetection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stopOnFirstDetectionTipText() {
    return
        "If set to true, the detection process will be stopped as soon as "
      + "one of the sub-detecors detected an outlier.";
  }

  /**
   * Updates the database connection in the outlier detectors.
   */
  @Override
  protected void updateDatabaseConnection() {
    for (AbstractOutlierDetector detector: m_Detectors) {
      if (detector instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) detector).setDatabaseConnection(getDatabaseConnection());
    }
  }

  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(T data) {
    List<String>	result;
    int			i;
    List<String>	detection;

    result = new ArrayList<String>();

    for (i = 0; i < m_Detectors.length; i++) {
      detection = AbstractOutlierDetector.detect(m_Detectors[i].shallowCopy(true), data);
      if (detection.size() > 0) {
	result.addAll(detection);
	if (m_StopOnFirstDetection)
	  break;
      }
    }

    if (isLoggingEnabled()) {
      if (result.size() > 0)
	getLogger().info(data + " - " + getClass().getName() + ": " + result);
    }

    return result;
  }
}
