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
 *    AbstractScriptedActor.java
 *    Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.FileBasedScriptingWithOptions;

import java.util.Hashtable;

/**
 * Abstract ancestor for actors that execute external scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptedActor
  extends AbstractActor
  implements FileBasedScriptingWithOptions {

  /** for serialization. */
  private static final long serialVersionUID = -8187233244973711251L;

  /** the key for storing the script object in the backup. */
  public final static String BACKUP_SCRIPTOBJECT = "script object";

  /** the script. */
  protected PlaceholderFile m_ScriptFile;

  /** the options for the script. */
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
	    new BaseText());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "scriptFile", new String(m_ScriptFile + " " + m_ScriptOptions).trim());
  }

  /**
   * Sets the script file.
   *
   * @param value 	the script
   */
  public void setScriptFile(PlaceholderFile value) {
    m_ScriptFile = value;
    reset();
  }

  /**
   * Gets the script file.
   *
   * @return 		the script
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
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    pruneBackup(BACKUP_SCRIPTOBJECT);
  }
  
  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_ScriptObject != null)
      result.put(BACKUP_SCRIPTOBJECT, m_ScriptObject);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_SCRIPTOBJECT)) {
      m_ScriptObject = state.get(BACKUP_SCRIPTOBJECT);
      state.remove(BACKUP_SCRIPTOBJECT);
    }

    super.restoreState(state);
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

    result = null;
    
    if (m_ScriptObject == null) {
      result = loadScriptObject();
      if (result == null)
	result = checkScriptObject();
      if ((result == null) && (m_ScriptObject != null)) {
	if (m_ScriptObject instanceof Actor) {
	  ((Actor) m_ScriptObject).setParent(this);
	  ((Actor) m_ScriptObject).setVariables(getVariables());
	  ((Actor) m_ScriptObject).setLoggingLevel(getLoggingLevel());
	}
      }
    }

    return result;
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

    if (result == null)
      result = initScriptObject();

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ScriptObject != null) {
      if (m_ScriptObject instanceof Actor)
	((Actor) m_ScriptObject).stopExecution();;
    }
    
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_ScriptObject = null;
  }
}
