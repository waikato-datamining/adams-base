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
 * AbstractAddTemplate.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.keyboardaction;

import adams.flow.template.AbstractActorTemplate;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for actions that add a template.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAddTemplate
  extends AbstractKeyboardAction {

  private static final long serialVersionUID = 9158512844896786075L;

  /** the template to use. */
  protected AbstractActorTemplate m_Template;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "template", "template",
      getDefaultTemplate());
  }

  /**
   * Returns the default template of the action.
   *
   * @return 		the default
   */
  protected abstract AbstractActorTemplate getDefaultTemplate();

  /**
   * Sets the template of the action.
   *
   * @param value 	the template
   */
  public void setTemplate(AbstractActorTemplate value) {
      m_Template = value;
    reset();
  }

  /**
   * Returns the template of the action.
   *
   * @return 		the template
   */
  public AbstractActorTemplate getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String templateTipText() {
    return "The template to add.";
  }

  /**
   * Checks whether the current state is suitable.
   *
   * @param state	the current state
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(StateContainer state) {
    String	result;

    result = super.check(state);

    if (result == null) {
      if (!state.editable)
	result = "Flow not editable";
      else if (state.selPath == null)
	result = "Not actor selected";
    }

    return result;
  }
}
