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
 * ForceFlush.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.ClassCrossReference;
import adams.core.ClassLister;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.FlushSupporter;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.flow.core.Unknown;
import adams.flow.core.actorfilter.Match;
import adams.flow.transformer.AbstractTransformer;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Forces all actors that implement adams.flow.core.FlushSupporter<br>
 * <br>
 * See also:<br>
 * adams.flow.sink.DumpFile
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: ForceFlush
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
 * <pre>-scope &lt;ROOT|STOP_RESTRICTOR|PARENT&gt; (property: scope)
 * &nbsp;&nbsp;&nbsp;The scope of the flush.
 * &nbsp;&nbsp;&nbsp;default: PARENT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ForceFlush
  extends AbstractTransformer
  implements ClassCrossReference  {

  private static final long serialVersionUID = -6947094767710340895L;

  /**
   * The scope for the flushing.
   */
  public enum FlushScope {
    ROOT,
    STOP_RESTRICTOR,
    PARENT,
  }

  /** the scope of the flush. */
  protected FlushScope m_Scope;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forces all actors that implement " + Utils.classToString(FlushSupporter.class);
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return ClassLister.getSingleton().getClasses(Actor.class, FlushSupporter.class);
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "scope", "scope",
      FlushScope.PARENT);
  }

  /**
   * Sets the scope for the flush.
   *
   * @param value	the scope
   */
  public void setScope(FlushScope value) {
    m_Scope = value;
    reset();
  }

  /**
   * Returns the scope for the flush.
   *
   * @return		the scope
   */
  public FlushScope getScope() {
    return m_Scope;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeTipText() {
    return "The scope of the flush.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "scope", m_Scope, "scope: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Actor	start;
    List<Actor> actors;

    switch (m_Scope) {
      case ROOT:
        start = getRoot();
        break;
      case STOP_RESTRICTOR:
	start = StopHelper.getStopActor(this, StopMode.STOP_RESTRICTOR);
        break;
      case PARENT:
        start = getParent();
        break;
      default:
        throw new IllegalStateException("Unhandled flush scope: " + m_Scope);
    }

    actors = ActorUtils.enumerate(start, new Match(FlushSupporter.class));
    if (actors.size() == 0) {
      getLogger().warning("No flush supporters in scope " + m_Scope);
    }
    else {
      for (Actor actor: actors) {
        if (isLoggingEnabled())
          getLogger().info("Flushing: " + actor.getFullName());
	((FlushSupporter) actor).performFlush();
      }
    }

    m_OutputToken = m_InputToken;

    return null;
  }
}
