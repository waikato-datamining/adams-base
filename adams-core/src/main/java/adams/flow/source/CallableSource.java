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
 * CallableSource.java
 * Copyright (C) 2009-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.AbstractCallableActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorReference;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Broadcasts the generated output tokens from the source.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: CallableSource
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-optional &lt;boolean&gt; (property: optional)
 * &nbsp;&nbsp;&nbsp;If enabled, then the callable actor is optional, ie no error is raised if 
 * &nbsp;&nbsp;&nbsp;not found, merely ignored.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CallableSource
  extends AbstractCallableActor
  implements OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -99235787254225765L;

  /**
   * Default constructor.
   */
  public CallableSource() {
    super();
  }

  /**
   * Allows setting the callable name.
   *
   * @param name	the reference to use
   */
  public CallableSource(CallableActorReference name) {
    this();
    setCallableName(name);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Broadcasts the generated output tokens from the source.";
  }

  /**
   * Tries to find the callable actor referenced by its global name.
   * Makes sure that the actor produces output.
   *
   * @return		the callable actor or null if not found
   */
  @Override
  protected Actor findCallableActor() {
    Actor result;

    result = super.findCallableActor();

    if (result != null) {
      if (!(ActorUtils.isSource(result))) {
        m_FindCallableActorError = "Callable actor '" + result.getFullName() + "' is not a source" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName());
	getLogger().severe(m_FindCallableActorError);
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		depends on the callable actor
   */
  public Class[] generates() {
    if (m_CallableActor != null)
      return ((OutputProducer) m_CallableActor).generates();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Executes the callable actor. Derived classes might need to override this
   * method to ensure atomicity.
   *
   * @return		null if no error, otherwise error message
   */
  @Override
  protected String executeCallableActor() {
    String	result;

    result = m_CallableActor.execute();

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    return ((OutputProducer) m_CallableActor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_CallableActor != null) && ((OutputProducer) m_CallableActor).hasPendingOutput();
  }
}
