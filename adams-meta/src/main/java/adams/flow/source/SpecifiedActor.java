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
 * SpecifiedActor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorPath;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs the actor identified by the actor path.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Actor<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpecifiedActor
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-path &lt;adams.flow.core.ActorPath&gt; (property: path)
 * &nbsp;&nbsp;&nbsp;The path of the actor to output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-expand-variables &lt;boolean&gt; (property: expandVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, variables get expanded to their current values.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-no-sub-actors &lt;boolean&gt; (property: noSubActors)
 * &nbsp;&nbsp;&nbsp;If enabled, actor handlers will have their sub-actors removed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpecifiedActor
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 3823225606368312035L;

  /** the actor path. */
  protected ActorPath m_Path;

  /** whether to expand variables. */
  protected boolean m_ExpandVariables;

  /** whether to exclude any sub-actors (in case of actor handlers). */
  protected boolean m_NoSubActors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the actor identified by the actor path.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "path", "path",
      new ActorPath());

    m_OptionManager.add(
      "expand-variables", "expandVariables",
      false);

    m_OptionManager.add(
      "no-sub-actors", "noSubActors",
      false);
  }

  /**
   * Sets the path of the actor to output.
   *
   * @param value 	the path
   */
  public void setPath(ActorPath value) {
    m_Path = value;
    reset();
  }

  /**
   * Returns the path of the actor to output.
   *
   * @return 		the path
   */
  public ActorPath getPath() {
    return m_Path;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pathTipText() {
    return "The path of the actor to output.";
  }

  /**
   * Sets whether to expand variables using their current values.
   *
   * @param value 	true if to expand
   */
  public void setExpandVariables(boolean value) {
    m_ExpandVariables = value;
    reset();
  }

  /**
   * Returns whether to expand variables using their current values.
   *
   * @return 		true if to expand
   */
  public boolean getExpandVariables() {
    return m_ExpandVariables;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandVariablesTipText() {
    return "If enabled, variables get expanded to their current values.";
  }

  /**
   * Sets whether to strip actor handlers of their sub-actors.
   *
   * @param value 	true if to strip sub-actors
   */
  public void setNoSubActors(boolean value) {
    m_NoSubActors = value;
    reset();
  }

  /**
   * Returns whether to strip actor handlers of their sub-actors.
   *
   * @return 		true if to strip sub-actors
   */
  public boolean getNoSubActors() {
    return m_NoSubActors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noSubActorsTipText() {
    return "If enabled, actor handlers will have their sub-actors removed.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "path", (m_Path.isEmpty() ? "-none-" : m_Path.toString()), "path: ");
    result += QuickInfoHelper.toString(this, "expandVariables", (m_ExpandVariables ? "expand vars" : "with vars"), ", ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Actor.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Actor	actor;

    result = null;

    actor = ActorUtils.locate(m_Path, getRoot(), true, false);
    if (actor == null) {
      result = "Failed to locate actor: " + m_Path;
    }
    else {
      if (m_ExpandVariables)
        actor = actor.shallowCopy(true);
      if (m_NoSubActors)
        actor = ActorUtils.strip(actor);
    }

    if (actor != null)
      m_OutputToken = new Token(actor);

    return result;
  }
}
