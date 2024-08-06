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
 * AbstractWekaPackageManagerAction.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekapackagemanageraction;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for package manager actions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWekaPackageManagerAction
  extends AbstractOptionHandler
  implements QuickInfoSupporter, FlowContextHandler {

  private static final long serialVersionUID = -6652797812165758692L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * The types of data the action accepts.
   *
   * @return		the input types
   */
  public abstract Class[] accepts();

  /**
   * The types of data the action generates.
   *
   * @return		the output types
   */
  public abstract Class[] generates();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks.
   *
   * @param input 	the input to process
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Object input) {
    Compatibility	comp;

    if (m_FlowContext == null)
      return "No flow context set!";

    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{input.getClass()}, accepts()))
      return "Input (" + Utils.classToString(input) + ") cannot be processed (accepts only: " + Utils.classesToString(accepts()) + ")!";

    return null;
  }

  /**
   * Executes the action.
   *
   * @param input 	the input to process
   * @param errors	for collecting errors
   * @return		the generated output, null if failed to generated
   */
  public abstract Object doExecute(Object input, MessageCollection errors);

  /**
   * Executes the action.
   *
   * @param input 	the input to process
   * @param errors	for collecting errors
   * @return		the generated output, null if failed to generated
   */
  public Object execute(Object input, MessageCollection errors) {
    String	msg;

    msg = check(input);
    if (msg != null) {
      errors.add(msg);
      return null;
    }
    else {
      return doExecute(input, errors);
    }
  }
}
