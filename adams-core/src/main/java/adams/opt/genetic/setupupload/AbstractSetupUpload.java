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
 * AbstractSetupUpload.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.opt.genetic.setupupload;

import adams.core.Properties;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

import java.util.Map;

/**
 * Ancestor for schemes that upload the setup of a genetic algorithm.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSetupUpload
  extends AbstractOptionHandler
  implements FlowContextHandler {

  private static final long serialVersionUID = 7070122955051739777L;

  /** the key for the measure. */
  public final static String KEY_MEASURE = "Measure";

  /** the key for the fitness. */
  public final static String KEY_FITNESS = "Fitness";

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the name of the experiment. */
  protected String m_Experiment;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "experiment", "experiment",
      "");
  }

  /**
   * Sets the experiment name to use.
   *
   * @param value 	the name
   */
  public void setExperiment(String value) {
    m_Experiment = value;
    reset();
  }

  /**
   * Returns the experiment name to use.
   *
   * @return 		the name
   */
  public String getExperiment() {
    return m_Experiment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String experimentTipText() {
    return "The name of the experiment to use when uploading the setup.";
  }

  /**
   * Sets the flow context, if any.
   *
   * @param value	the context
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Return the flow context, if any.
   *
   * @return		the context, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  public abstract boolean requiresFlowContext();

  /**
   * Uploads the setup.
   *
   * @param setup	the setup data to upload
   * @return		null if successful, otherwise error message
   */
  protected abstract String doUpload(Map<String,Object> setup);

  /**
   * Uploads the setup.
   *
   * @param setup	the setup data to upload
   * @return		null if successful, otherwise error message
   */
  public String upload(Map<String,Object> setup) {
    if (requiresFlowContext() && (m_FlowContext == null))
      return "No flow context set, upload failed!";
    return doUpload(setup);
  }

  /**
   * Turns the setup map into a properties object.
   *
   * @param setup	the setup to convert
   * @return		the generated properties
   */
  public static Properties toProperties(Map<String,Object> setup) {
    Properties	result;
    Object		value;

    result = new Properties();
    for (String key: setup.keySet()) {
      value = setup.get(key);
      if (value instanceof Integer)
	result.setInteger(key, (Integer) value);
      else if (value instanceof Double)
	result.setDouble(key, (Double) value);
      else
	result.setProperty(key, value.toString());
    }

    return result;
  }
}
