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
 * AbstractMultiRowProcessorPlugin.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor;

import adams.core.GlobalInfoSupporter;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Ancestor for MultiRowProcessor plugins.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiRowProcessorPlugin
    implements Serializable, OptionHandler, GlobalInfoSupporter {

  private static final long serialVersionUID = -8959596035616332968L;

  protected static String DEBUG = "debug";

  /** whether to output debugging information. */
  protected boolean m_Debug;

  /**
   * Returns a string describing the processor.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Resets the scheme.
   * <br>
   * Default implementation does nothing.
   */
  protected void reset() {
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector result;

    result = new Vector();
    WekaOptionUtils.addFlag(result, debugTipText(), DEBUG);

    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setDebug(Utils.getFlag(DEBUG, options));
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, DEBUG, getDebug());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets whether to output debugging information.
   *
   * @param value	true if in debug mode
   */
  public void setDebug(boolean value) {
    m_Debug = value;
    reset();
  }

  /**
   * Returns whether to output debugging information.
   *
   * @return		true if in debug mode
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String debugTipText() {
    return "If enabled, debugging information is being printed to stderr.";
  }

  /**
   * Outputs a debugging message on stderr.
   *
   * @param msg		the message
   */
  protected void debugMsg(String msg) {
    System.err.println(getClass().getName() + ": " + msg);
  }
}
