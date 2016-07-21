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
 *    Groovy.java
 *    Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.scripting.GroovyScript;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * A boolean condition that uses the condition defined in a Groovy script.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-options &lt;java.lang.String&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the script.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-inline-script &lt;adams.core.scripting.GroovyScript&gt; (property: inlineScript)
 * &nbsp;&nbsp;&nbsp;The inline script, if not using an external script file.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.core.scripting.Groovy
 */
public class Groovy
  extends AbstractScriptedCondition {

  /** for serialization. */
  private static final long serialVersionUID = 4394482470846849594L;

  /** the loaded script object. */
  protected transient BooleanCondition m_ConditionObject;
  
  /** the inline script. */
  protected GroovyScript m_InlineScript;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A boolean condition that uses the condition defined in a Groovy script.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (QuickInfoHelper.hasVariable(this, "scriptFile") || !m_ScriptFile.isDirectory())
      return super.getQuickInfo();
    else
      return QuickInfoHelper.toString(this, "inlineScript", Shortening.shortenEnd(m_InlineScript.stringValue(), 50));
  }

  /**
   * Returns the default inline script.
   * 
   * @return		the default script
   */
  protected GroovyScript getDefaultInlineScript() {
    return new GroovyScript();
  }

  /**
   * Sets the inline script to use instead of the external script file.
   *
   * @param value 	the inline script
   */
  public void setInlineScript(GroovyScript value) {
    m_InlineScript = value;
    reset();
  }

  /**
   * Gets the inline script to use instead of the external script file.
   *
   * @return 		the inline script
   */
  public GroovyScript getInlineScript() {
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
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = adams.core.scripting.Groovy.getSingleton().loadScriptObject(
	BooleanCondition.class, 
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
    m_ConditionObject = (BooleanCondition) m_ScriptObject;
    return null;
  }

  /**
   * Runs the script evaluation.
   *
   * @return		the boolean result of the evaluation
   */
  @Override
  protected boolean doScriptEvaluate(Actor owner, Token token) {
    return m_ConditionObject.evaluate(owner, token);
  }
}
