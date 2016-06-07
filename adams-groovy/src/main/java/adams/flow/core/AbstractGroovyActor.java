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
 *    AbstractGroovyActor.java
 *    Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.scripting.Groovy;
import adams.core.scripting.GroovyScript;

/**
 * Abstract ancestor for actors that execute Groovy scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Groovy
 */
public abstract class AbstractGroovyActor
  extends AbstractScriptedActor {

  /** for serialization. */
  private static final long serialVersionUID = -5904986133981940404L;

  /** the loaded script object. */
  protected transient Actor m_ActorObject;
  
  /** the inline script. */
  protected GroovyScript m_InlineScript;

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
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActorObject = null;
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
      return QuickInfoHelper.toString(this, "inlineScript", Utils.shorten(m_InlineScript.stringValue(), 50));
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
        "The options for the Groovy script; must consist of 'key=value' pairs "
      + "separated by blanks; the value of 'key' can be accessed via the "
      + "'getAdditionalOptions().getXYZ(\"key\")' method in the Groovy actor.";
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

    result         = Groovy.getSingleton().loadScriptObject(Actor.class, m_ScriptFile, m_InlineScript, m_ScriptOptions, getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }
  
  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    String	result;
    Actor	script;

    result = null;
    script = (Actor) m_ScriptObject;

    if (ActorUtils.isStandalone(this) && !ActorUtils.isStandalone(script))
      result = "Script object is not a singleton!";
    else if (ActorUtils.isSource(this) && !ActorUtils.isSource(script))
      result = "Script object is not a source!";
    else if (ActorUtils.isTransformer(this) && !ActorUtils.isTransformer(script))
      result = "Script object is not a transformer!";
    else if (ActorUtils.isSink(this) && !ActorUtils.isSink(script))
      result = "Script object is not a sink!";

    return result;
  }

  /**
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String preExecute() {
    String	result;

    result = super.preExecute();

    if (result == null) {
      if (m_ActorObject == null) {
        m_ActorObject = (Actor) m_ScriptObject;
        result = m_ActorObject.setUp();
      }
    }

    return result;
  }
  
  /**
   * Updates the script options.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String updateScriptOptions() {
    if (getScriptOptions().getValue().length() == 0)
      return null;
    
    try {
      AdditionalOptionsHandlerUtils.setOptions(m_ActorObject, getScriptOptions().getValue(), getVariables());
      return null;
    }
    catch (Exception e) {
      return handleException("Failed to update options", e);
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    if (m_ActorObject != null)
      m_ActorObject.destroy();
    m_ActorObject = null;
  }
}
