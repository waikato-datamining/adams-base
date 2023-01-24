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
 * Log.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command.stdout;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.flow.core.FlowContextHandler;

/**
 * Outputs the data received from the command's stdout via its logger instance.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Log
  extends AbstractStdOutProcessor {

  private static final long serialVersionUID = -2194306680981658479L;

  public final static String PH_FULLNAME = "{FULLNAME}";

  public final static String PH_NAME = "{NAME}";

  /** the logging prefix to use. */
  protected String m_LoggingPrefixFormat;

  /** the logging prefix. */
  protected String m_LoggingPrefix;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the data received from the command's stdout via its logger instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "logging-prefix-format", "loggingPrefixFormat",
      PH_FULLNAME + ".stdout");
  }

  /**
   * Returns the default logging level to use.
   *
   * @return		the logging level
   */
  @Override
  protected LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.INFO;
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_LoggingPrefix);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Sets the format for the logging prefix.
   *
   * @param value	the format
   */
  public void setLoggingPrefixFormat(String value) {
    m_LoggingPrefixFormat = value;
    reset();
  }

  /**
   * Returns the format for the logging prefix.
   *
   * @return		the name
   */
  public String getLoggingPrefixFormat() {
    return m_LoggingPrefixFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingPrefixFormatTipText() {
    return "The format for the logging prefix; supported placeholders: "
      + PH_FULLNAME + ": owning actor's full name (incl tree path), " + PH_NAME + ": owning actor's name.";
  }

  /**
   * Configures the handler.
   *
   * @param owner 	the owning command
   * @return 		null if successfully setup, otherwise error message
   */
  public String setUp(FlowContextHandler owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      m_LoggingPrefix = m_LoggingPrefixFormat;
      m_LoggingPrefix = m_LoggingPrefix.replace(PH_FULLNAME, owner.getFlowContext().getFullName());
      m_LoggingPrefix = m_LoggingPrefix.replace(PH_NAME, owner.getFlowContext().getName());
      m_Logger        = null;
    }

    return result;
  }

  /**
   * Processes the stdout output received when in async mode.
   *
   * @param output the output to process
   */
  @Override
  public void processAsync(String output) {
    if (isLoggingEnabled())
      getLogger().info(output);
  }

  /**
   * Processes the stdout output received when in blocking mode.
   *
   * @param output the output to process
   */
  @Override
  public void processBlocking(String output) {
    processAsync(output);
  }
}
