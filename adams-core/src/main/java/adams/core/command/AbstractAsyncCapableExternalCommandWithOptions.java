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
 * AbstractAsyncCapableExternalCommandWithOptions.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.command;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.option.OptionUtils;

import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for commands that can be run in async mode that support options.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAsyncCapableExternalCommandWithOptions
  extends AbstractAsyncCapableExternalCommand
  implements ExternalCommandWithOptions {

  private static final long serialVersionUID = -456075395038165094L;

  /** the options for the command. */
  protected BaseString[] m_Options;

  /** the options as single string. */
  protected BaseText m_OptionsString;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "option", "options",
      new BaseString[0]);

    m_OptionManager.add(
      "options-string", "optionsString",
      new BaseText());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    if (!m_OptionsString.isEmpty() || getOptionManager().hasVariableForProperty("optionsString"))
      result += QuickInfoHelper.toString(this, "optionsString", (m_OptionsString.isEmpty() ? "-none-" : m_OptionsString), ", options string: ");
    else
      result += QuickInfoHelper.toString(this, "options", m_Options, ", options: ");

    return result;
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(List<String> value) {
    setOptions(value.toArray(new String[0]));
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptions(String[] value) {
    setOptions((BaseString[]) BaseObject.toObjectArray(value, BaseString.class));
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  @Override
  public void setOptions(BaseString[] value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the options for the command.
   *
   * @return		the options
   */
  @Override
  public BaseString[] getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String optionsTipText() {
    return "The options for the command; variables get expanded automatically.";
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  public void setOptionsString(String value) {
    setOptionsString(new BaseText(value));
  }

  /**
   * Sets the options for the command.
   *
   * @param value	the options
   */
  @Override
  public void setOptionsString(BaseText value) {
    m_OptionsString = value;
    reset();
  }

  /**
   * Returns the options for the command as single string.
   *
   * @return		the options
   */
  @Override
  public BaseText getOptionsString() {
    return m_OptionsString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String optionsStringTipText() {
    return "The options for the command as a single string; overrides the options array; variables get expanded automatically.";
  }

  /**
   * Returns the actual options to use. The options string takes precendence over the array.
   *
   * @return		the options
   */
  @Override
  public String[] getActualOptions() {
    String[]	result;
    int		i;
    Variables vars;

    vars = m_FlowContext.getVariables();
    try {
      if (!m_OptionsString.isEmpty()) {
	result = OptionUtils.splitOptions(vars.expand(m_OptionsString.getValue()));
      }
      else {
	result = BaseObject.toStringArray(m_Options);
	for (i = 0; i < result.length; i++)
	  result[i] = vars.expand(result[i]);
      }
      return result;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse options!", e);
      return new String[0];
    }
  }
}
