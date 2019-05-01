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
 * FindInFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.exception.ExceptionHandler;
import adams.core.io.filesearch.AbstractFileSearchHandler;
import adams.core.io.filesearch.FileSearchHandler;
import adams.core.io.filesearch.RegExpFileSearchHandler;
import adams.core.io.filesearch.TextFileSearchHandler;
import adams.flow.core.Token;

import java.io.File;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Searches the incoming (text) file for the specified search string, output the boolean search result.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Boolean<br>
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
 * &nbsp;&nbsp;&nbsp;default: FindInFile
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
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The text to search for.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-case-sensitive &lt;boolean&gt; (property: caseSensitive)
 * &nbsp;&nbsp;&nbsp;If enabled, the search is performed in case-sensitive fashion.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-regexp &lt;boolean&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;If enabled, the search string is interpreted as a regular expression.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-custom-handler &lt;boolean&gt; (property: useCustomHandler)
 * &nbsp;&nbsp;&nbsp;If enabled, the specified file search handler is used instead of auto-detected
 * &nbsp;&nbsp;&nbsp;one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-handler &lt;adams.core.io.filesearch.FileSearchHandler&gt; (property: customHandler)
 * &nbsp;&nbsp;&nbsp;The custom file search handler to use (if enabled).
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.filesearch.TextFileSearchHandler
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FindInFile
  extends AbstractTransformer {

  private static final long serialVersionUID = 9091439366419647189L;

  /** the text to look for. */
  protected String m_Find;

  /** whether the search is case-sensitive. */
  protected boolean m_CaseSensitive;

  /** whether the search string represents a regular expression (search handler must support that). */
  protected boolean m_RegExp;

  /** whether to use a specific file search handler rather than auto-detected one. */
  protected boolean m_UseCustomHandler;

  /** the handler to use. */
  protected FileSearchHandler m_CustomHandler;

  /** the actual handler in use. */
  protected FileSearchHandler m_ActualHandler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Searches the incoming (text) file for the specified search string, output the boolean search result.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      "");

    m_OptionManager.add(
      "case-sensitive", "caseSensitive",
      false);

    m_OptionManager.add(
      "regexp", "regExp",
      false);

    m_OptionManager.add(
      "use-custom-handler", "useCustomHandler",
      false);

    m_OptionManager.add(
      "custom-handler", "customHandler",
      new TextFileSearchHandler());
  }

  /**
   * Sets the search text.
   *
   * @param value 	the text
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the search text.
   *
   * @return 		the text
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The text to search for.";
  }

  /**
   * Sets whether the search is case-sensitive.
   *
   * @param value 	true if case-sensitive
   */
  public void setCaseSensitive(boolean value) {
    m_CaseSensitive = value;
    reset();
  }

  /**
   * Returns whether the search is case-sensitive.
   *
   * @return 		true if case-sensitive
   */
  public boolean getCaseSensitive() {
    return m_CaseSensitive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String caseSensitiveTipText() {
    return "If enabled, the search is performed in case-sensitive fashion.";
  }

  /**
   * Sets whether the search string is a regular expression.
   *
   * @param value 	true if regexp
   */
  public void setRegExp(boolean value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns whether the search string is a regular expression.
   *
   * @return 		true if regexp
   */
  public boolean getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "If enabled, the search string is interpreted as a regular expression.";
  }

  /**
   * Sets whether to use the custom handler instead of auto-detected one.
   *
   * @param value 	true if custom handler
   */
  public void setUseCustomHandler(boolean value) {
    m_UseCustomHandler = value;
    reset();
  }

  /**
   * Returns whether to use the custom handler instead of auto-detected one.
   *
   * @return 		true if custom handler
   */
  public boolean getUseCustomHandler() {
    return m_UseCustomHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomHandlerTipText() {
    return "If enabled, the specified file search handler is used instead of auto-detected one.";
  }

  /**
   * Sets the file search handler to use.
   *
   * @param value 	the handler
   */
  public void setCustomHandler(FileSearchHandler value) {
    m_CustomHandler = value;
    reset();
  }

  /**
   * Returns the file search handler to use.
   *
   * @return 		the handler
   */
  public FileSearchHandler getCustomHandler() {
    return m_CustomHandler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customHandlerTipText() {
    return "The custom file search handler to use (if enabled).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "find", (m_Find.isEmpty() ? "-empty-" : m_Find), "find: ");
    result += QuickInfoHelper.toString(this, "caseSensitive", m_CaseSensitive, "case-sensitive", ", ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp", ", ");
    if (m_UseCustomHandler)
      result += QuickInfoHelper.toString(this, "customHandler", m_CustomHandler, ", handler: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Boolean.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_UseCustomHandler && m_RegExp) {
        if (!(m_CustomHandler instanceof RegExpFileSearchHandler))
          result = "Custom file search handler " + Utils.classToString(m_CustomHandler) + " does not support regular expression searching!";
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		file;
    ExceptionHandler 	exHandler;
    boolean		found;

    result = null;

    // get file
    file = null;
    if (m_InputToken.hasPayload(String.class))
      file = m_InputToken.getPayload(String.class);
    else if (m_InputToken.hasPayload(File.class))
      file = m_InputToken.getPayload(File.class).getAbsolutePath();
    else
      result = m_InputToken.unhandledData();

    // determine handler
    m_ActualHandler = null;
    if (result == null) {
      if (m_UseCustomHandler) {
	m_ActualHandler = m_CustomHandler;
      }
      else {
	m_ActualHandler = AbstractFileSearchHandler.getHandlerForFile(file);
	if (isLoggingEnabled())
	  getLogger().info("File '" + file + " -> handler: " + Utils.classToString(m_ActualHandler));
	if (m_ActualHandler == null)
	  result = "Failed to determine file search handler for file: " + file;
	else if (m_RegExp && !(m_ActualHandler instanceof RegExpFileSearchHandler))
	  result = "Auto-detected file search handler " + Utils.classToString(m_ActualHandler) + " does not support regular expression searching!";
      }
    }

    if (result == null) {
      try {
        exHandler = (String msg, Throwable t) -> getLogger().log(Level.SEVERE, msg, t);
        if (m_RegExp && (m_ActualHandler instanceof RegExpFileSearchHandler))
	  found = ((RegExpFileSearchHandler) m_ActualHandler).searchRegExp(file, m_Find, m_CaseSensitive, exHandler);
        else
	  found = m_ActualHandler.searchFile(file, m_Find, m_CaseSensitive, exHandler);
	m_ActualHandler = null;
        m_OutputToken   = new Token(found);
      }
      catch (Exception e) {
	result = handleException("Failed to search: " + file, e);
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ActualHandler != null)
      m_ActualHandler.stopExecution();
    super.stopExecution();
  }
}
