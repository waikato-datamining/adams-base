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
 *    AbstractScriptedConversion.java
 *    Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseText;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.FileBasedScriptingWithOptions;

/**
 * Abstract ancestor for actors that execute external scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractScriptedConversion
  extends AbstractConversion
  implements ConversionWithInitialization, FileBasedScriptingWithOptions {

  /** for serialization. */
  private static final long serialVersionUID = -8187233244973711251L;

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
   * Resets the conversion.
   */
  @Override
  protected void reset() {
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
    return QuickInfoHelper.toString(this, "scriptFile", (m_ScriptFile + " " + m_ScriptOptions).trim(), null);
  }

  /**
   * Sets the script file.
   *
   * @param value 	the script
   */
  @Override
  public void setScriptFile(PlaceholderFile value) {
    m_ScriptFile = value;
    reset();
  }

  /**
   * Gets the script file.
   *
   * @return 		the script
   */
  @Override
  public PlaceholderFile getScriptFile() {
    return m_ScriptFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptFileTipText() {
    return "The script file to load and execute.";
  }

  /**
   * Sets the script options.
   *
   * @param value 	the options
   */
  @Override
  public void setScriptOptions(BaseText value) {
    m_ScriptOptions = value.getValue();
    reset();
  }

  /**
   * Gets the script options.
   *
   * @return 		the options
   */
  @Override
  public BaseText getScriptOptions() {
    return new BaseText(m_ScriptOptions);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
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
   * Method for checking whether we can setUp the script.
   *
   * @return		true if it can be setUp
   */
  protected boolean canSetUpScript() {
    return FileUtils.fileExists(m_ScriptFile);
  }

  /**
   * Performs some initializations.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    if (canSetUpScript())
      return initScriptObject();

    return null;
  }

  /**
   * Checks whether we still need to perform a setup.
   *
   * @return		true if {@link #setUp()} call is necessary
   */
  @Override
  public boolean requiresSetUp() {
    return (m_ScriptObject == null);
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_ScriptObject = null;
  }
}
