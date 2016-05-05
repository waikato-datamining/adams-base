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
 * AbstractClusterer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.model.clustering;

import adams.core.DateUtils;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.ml.capabilities.Capabilities;
import adams.ml.capabilities.CapabilitiesHelper;
import adams.ml.data.Dataset;
import adams.ml.model.classification.ClassificationModel;
import adams.ml.model.classification.Classifier;

import java.util.Date;

/**
 * Ancestor for clustering algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClusterer
  extends AbstractOptionHandler
  implements Classifier {

  private static final long serialVersionUID = 1493597879425680024L;

  /** whether to use strict capability tests. */
  protected boolean m_StrictCapabilities;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "strict-capabilities", "strictCapabilities",
	    false);
  }

  /**
   * Sets whether to use a strict capabilities check.
   *
   * @param value	true if to use strict check
   */
  public void setStrictCapabilities(boolean value) {
    m_StrictCapabilities = value;
    reset();
  }

  /**
   * Returns whether to use a strict capabilities check.
   *
   * @return		true if to use strict check
   */
  public boolean getStrictCapabilities() {
    return m_StrictCapabilities;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strictCapabilitiesTipText() {
    return
      "If enabled, a strict capabilities test is performed; otherwise, it is "
	+ "attempted to adjust the data to fit the algorithm's capabilities.";
  }

  /**
   * Returns the algorithm's capabilities in terms of data.
   *
   * @return		the algorithm's capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.setMinClassColumns(0);
    result.setMaxClassColumns(0);

    return result;
  }

  /**
   * Checks whether the data can be handled.
   *
   * @param data	the data to check
   * @param strict	whether to perform a strict check
   * @return		null if data can be handled, otherwise error message
   */
  public String handles(Dataset data, boolean strict) {
    String	result;
    Dataset	adjusted;

    result = CapabilitiesHelper.handles(getCapabilities(), data);

    if (!strict && (result != null)) {
      try {
	adjusted = CapabilitiesHelper.adjust(getCapabilities(), data);
	result   = CapabilitiesHelper.handles(getCapabilities(), adjusted);
      }
      catch (Exception e) {
	result += "\nAdjusting of dataset failed with: " + Utils.throwableToString(e);
      }
    }

    return result;
  }

  /**
   * Performs checks on the data.
   *
   * @param data	the data to check
   * @return		the potentially adjusted data
   * @throws Exception	if checks fail
   */
  protected Dataset check(Dataset data) throws Exception {
    String	msg;

    msg = handles(data, m_StrictCapabilities);
    if (msg != null)
      throw new Exception("Capabilities check failed: " + msg);

    if (!m_StrictCapabilities)
      return CapabilitiesHelper.adjust(getCapabilities(), data);
    else
      return data;
  }

  /**
   * Builds a model from the data.
   *
   * @param data	the data to use for building the model
   * @return		the generated model
   * @throws Exception	if the build fails
   */
  protected abstract ClassificationModel doBuildModel(Dataset data) throws Exception;

  /**
   * Builds a model from the data.
   *
   * @param data	the data to use for building the model
   * @return		the generated model
   * @throws Exception	if the build fails
   */
  public ClassificationModel buildModel(Dataset data) throws Exception {
    ClassificationModel		result;

    // check data
    if (isLoggingEnabled())
      getLogger().info("Performing checks");
    data = check(data);
    if (isLoggingEnabled())
      getLogger().info("Checks passed");

    // build
    if (isLoggingEnabled())
      getLogger().fine("Build start: " + DateUtils.getTimestampFormatterMsecs().format(new Date()));
    result = doBuildModel(data);
    if (isLoggingEnabled())
      getLogger().fine("Build end: " + DateUtils.getTimestampFormatterMsecs().format(new Date()));

    return result;
  }
}
