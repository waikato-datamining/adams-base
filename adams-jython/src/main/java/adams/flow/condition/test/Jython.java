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
 *    Jython.java
 *    Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.condition.test;

import java.util.Hashtable;

import adams.core.scripting.JythonScript;

/**
 <!-- globalinfo-start -->
 * A condition that uses the condition defined in an external Jython script.
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
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-options &lt;java.lang.String&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the Jython script; must consist of 'key=value' pairs separated
 * &nbsp;&nbsp;&nbsp;by blanks; the value of 'key' can be accessed via the 'getAdditionalOption
 * &nbsp;&nbsp;&nbsp;(String)' in the Jython actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.scripting.Jython
 */
public class Jython
  extends AbstractScriptedCondition {

  /** for serialization. */
  private static final long serialVersionUID = -2562159780167388413L;

  /** the loaded script object. */
  protected transient AbstractTestCondition m_ConditionObject;
  
  /** the inline script. */
  protected JythonScript m_InlineScript;

  /** for storing the additional options. */
  protected Hashtable<String,String> m_AdditionalOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A condition that uses the condition defined in an external Jython script.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AdditionalOptions = new Hashtable<String,String>();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "inline-script", "inlineScript",
	    getDefaultInlineScript());
  }

  /**
   * Returns the default inline script.
   * 
   * @return		the default script
   */
  protected JythonScript getDefaultInlineScript() {
    return new JythonScript();
  }

  /**
   * Sets the inline script to use instead of the external script file.
   *
   * @param value 	the inline script
   */
  public void setInlineScript(JythonScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Gets the inline script to use instead of the external script file.
   *
   * @return 		the inline script
   */
  public JythonScript getInlineScript() {
    return m_InlineScript;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String inlineScriptTipText() {
    return "The inline script, if not using an external script file.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
        "The options for the Jython script; must consist of 'key=value' pairs "
      + "separated by blanks; the value of 'key' can be accessed via the "
      + "'getAdditionalOption(String)' in the Jython actor.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = adams.core.scripting.Jython.getSingleton().loadScriptObject(
	AbstractTestCondition.class, 
	m_ScriptFile, 
	m_InlineScript, 
	m_ScriptOptions,
	getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		always null, i.e., OK
   */
  @Override
  protected String checkScriptObject() {
    m_ConditionObject = (AbstractTestCondition) m_ScriptObject;
    return null;
  }

  /**
   * Runs the script test.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performScriptTest() {
    return m_ConditionObject.getTestResult();
  }

  /**
   * Sets the additional options.
   *
   * @param options	the options (name &lt;-&gt;value relation)
   */
  public void setAdditionalOptions(Hashtable<String,String> options) {
    m_AdditionalOptions = (Hashtable<String,String>) options.clone();
  }

  /**
   * Returns the value associated with the (additional) option.
   *
   * @param name	the name of the additional option to retrieve
   * @param defValue	the default value
   * @return		the value or null if not available
   */
  public String getAdditionalOption(String name, String defValue) {
    if (m_AdditionalOptions.containsKey(name))
      return m_AdditionalOptions.get(name);
    else
      return defValue;
  }
}
