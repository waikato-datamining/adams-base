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
 * CallableSink.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractCallableActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Lets a callable sink consume the input tokens.
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
 * &nbsp;&nbsp;&nbsp;default: CallableSink
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
 * @version $Revision$
 */
public class CallableSink
  extends AbstractCallableActor
  implements InputConsumer {

  /** for serialization. */
  private static final long serialVersionUID = 4736661460553985100L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the token that is to be fed to the callable sink. */
  protected transient Token m_CurrentInput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lets a callable sink consume the input tokens.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   * Makes sure that the actor produces output.
   *
   * @return		the callable actor or null if not found
   */
  @Override
  protected AbstractActor findCallableActor() {
    AbstractActor	result;

    result = super.findCallableActor();

    if (result != null) {
      if (!(ActorUtils.isSink(result))) {
        m_FindCallableActorError = "Callable actor '" + result.getFullName() + "' is not a sink" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName());
	getLogger().severe(m_FindCallableActorError);
	result = null;
      }
    }

    return result;
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

    if (m_CurrentInput != null)
      result.put(BACKUP_CURRENT, m_CurrentInput);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENT)) {
      m_CurrentInput = (Token) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CurrentInput = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    if (m_CallableActor != null)
      return ((InputConsumer) m_CallableActor).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_CurrentInput = token;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_CurrentInput != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_CurrentInput;
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

    ((InputConsumer) m_CallableActor).input(m_CurrentInput);
    result = m_CallableActor.execute();

    return result;
  }
}
