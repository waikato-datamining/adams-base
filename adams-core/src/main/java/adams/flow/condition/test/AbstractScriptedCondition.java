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
 *    Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.flow.condition.test;

import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.scripting.FileBasedScriptingWithOptions;

/**
 * Abstract ancestor for test conditions that execute scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptedCondition
  extends AbstractTestCondition
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
	    new BaseText());
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
   * Runs the script test.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  protected abstract String performScriptTest();

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performTest() {
    String	result;

    result = null;

    if (m_ScriptObject == null)
      result = initScriptObject();

    if (result == null)
      result = performScriptTest();

    return result;
  }
}
