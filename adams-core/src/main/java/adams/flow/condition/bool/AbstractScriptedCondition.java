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
 *    AbstractScriptedCondition.java
 *    Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.condition.bool;

import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.FileBasedScriptingWithOptions;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 * Abstract ancestor for actors that execute scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptedCondition
  extends AbstractBooleanCondition 
  implements FileBasedScriptingWithOptions {

  /** for serialization. */
  private static final long serialVersionUID = -1266048092842841686L;

  /** the Groovy module. */
  protected PlaceholderFile m_ScriptFile;

  /** the options for the Groovy module. */
  protected String m_ScriptOptions;

  /** the loaded script object. */
  protected transient Object m_ScriptObject;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "script", "scriptFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "options", "scriptOptions",
	    new BaseText(""));
  }

  /**
   * Resets the condition.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    super.reset();

    m_ScriptObject = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	variable;

    variable = getOptionManager().getVariableForProperty("scriptFile");

    if (variable != null)
      return variable;
    else
      return new String(m_ScriptFile + " " + m_ScriptOptions).trim();
  }

  /**
   * Sets the Groovy module.
   *
   * @param value 	the Groovy module
   */
  public void setScriptFile(PlaceholderFile value) {
    m_ScriptFile = value;
    reset();
  }

  /**
   * Gets the Groovy module.
   *
   * @return 		the Groovy module
   */
  public PlaceholderFile getScriptFile() {
    return m_ScriptFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String scriptFileTipText() {
    return "The script file to load and execute.";
  }

  /**
   * Sets the script options.
   *
   * @param value 	the options
   */
  public void setScriptOptions(BaseText value) {
    m_ScriptOptions = value.getValue();
    reset();
  }

  /**
   * Gets the script options.
   *
   * @return 		the options
   */
  public BaseText getScriptOptions() {
    return new BaseText(m_ScriptOptions);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String scriptOptionsTipText() {
    return "The options for the script.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  protected abstract String loadScriptObject();

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  protected abstract String checkScriptObject();

  /**
   * Tries to initialize the scripts object, sets its options and performs
   * some checks.
   *
   * @return		null if OK, otherwise the error message
   */
  protected String initScriptObject() {
    String	result;

    result = loadScriptObject();
    if (result == null)
      result = checkScriptObject();

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);
    
    if (result == null)
      result = initScriptObject();

    return result;
  }

  /**
   * Runs the script evaluation.
   *
   * @return		the boolean result of the evaluation
   */
  protected abstract boolean doScriptEvaluate(Actor owner, Token token);

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    String	msg;
    
    if (m_ScriptObject == null) {
      msg = initScriptObject();
      if (msg != null)
	throw new IllegalStateException(msg);
    }
    
    return doScriptEvaluate(owner, token);
  }
}
