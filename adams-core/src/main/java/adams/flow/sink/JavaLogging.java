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
 * JavaLogging.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

/**
 <!-- globalinfo-start -->
 * Uses the Java logging framework to output the incoming data.<br>
 * Simply uses an object's 'toString()' method.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: JavaLogging
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-logger-name &lt;java.lang.String&gt; (property: loggerName)
 * &nbsp;&nbsp;&nbsp;The logger name to use; if left empty, the full actor name is used.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-logger-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggerLevel)
 * &nbsp;&nbsp;&nbsp;The level to use for the logger.
 * &nbsp;&nbsp;&nbsp;default: INFO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JavaLogging
  extends AbstractSink {

  private static final long serialVersionUID = -1648317940295223673L;

  /** the logger name. */
  protected String m_LoggerName;

  /** the logger level. */
  protected LoggingLevel m_LoggerLevel;

  /** the logger in use. */
  protected transient adams.core.logging.Logger m_ActualLogger;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the Java logging framework to output the incoming data.\n"
	+ "Simply uses an object's 'toString()' method.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "logger-name", "loggerName",
      "");

    m_OptionManager.add(
      "logger-level", "loggerLevel",
      LoggingLevel.INFO);
  }

  /**
   * Sets the logger name. Uses the full actor's name if left empty.
   *
   * @param value 	the name
   */
  public void setLoggerName(String value) {
    m_LoggerName = value;
    reset();
  }

  /**
   * Returns the logger name. Uses the full actor's name if left empty.
   *
   * @return 		the name
   */
  public String getLoggerName() {
    return m_LoggerName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggerNameTipText() {
    return "The logger name to use; if left empty, the full actor name is used.";
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public void setLoggerLevel(LoggingLevel value) {
    m_LoggerLevel = value;
    reset();
  }

  /**
   * Returns the logging level.
   *
   * @return		the level
   */
  public LoggingLevel getLoggerLevel() {
    return m_LoggerLevel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggerLevelTipText() {
    return "The level to use for the logger.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "loggerName", (m_LoggerName.isEmpty() ? "-actor name-" : m_LoggerName), "name: ");
    result += QuickInfoHelper.toString(this, "loggerLevel", m_LoggerLevel, ", level: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (m_ActualLogger == null) {
      m_ActualLogger = LoggingHelper.getLogger(m_LoggerName.isEmpty() ? getFullName() : m_LoggerName);
      m_ActualLogger.setLevel(m_LoggerLevel.getLevel());
    }
    if (m_InputToken.getPayload() != null)
      m_ActualLogger.log(m_LoggerLevel.getLevel(), m_InputToken.getPayload().toString());
    return null;
  }
}
