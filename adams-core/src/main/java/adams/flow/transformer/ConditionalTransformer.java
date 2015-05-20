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
 * ConditionalTransformer.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import adams.flow.control.AbstractConditionalActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Transformer that needs to fullfil a condition before being executed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ConditionalTransformer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-condition &lt;adams.flow.condition.AbstractCondition [options]&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition that has to be met before the actor can be executed.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.True
 * </pre>
 *
 * <pre>-execution-time (property: checkAtExecutionTime)
 * &nbsp;&nbsp;&nbsp;If set to true, then the condition is checked at execution time (whenever
 * &nbsp;&nbsp;&nbsp;the actor gets executed) and not during setup.
 * </pre>
 *
 * <pre>-execute-on-fail (property: executeOnFail)
 * &nbsp;&nbsp;&nbsp;If set to true, then the actor is only executed if the condition fails.
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.AbstractActor [options]&gt; (property: actor)
 * &nbsp;&nbsp;&nbsp;The base transformer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.PassThrough
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConditionalTransformer
  extends AbstractConditionalActor
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -9200572023682370972L;

  /** the key for storing the current input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the current token to process. */
  protected transient Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Transformer that needs to fullfil a condition before being executed.";
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  protected AbstractActor getDefaultActor() {
    return new PassThrough();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorTipText() {
    return "The base transformer to use.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.UNDEFINED, false);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      ((InputConsumer) m_BaseActor).input(m_InputToken);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!ActorUtils.isTransformer(m_BaseActor))
	result = "Base actor is not a transformer!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    if (m_BaseActor != null)
      return ((InputConsumer) m_BaseActor).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken = token;
    if (!m_CheckAtExecutionTime)
      ((InputConsumer) m_BaseActor).input(m_InputToken);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_BaseActor != null)
      return ((OutputProducer) m_BaseActor).generates();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Pre-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String preExecute() {
    String	result;

    result = super.preExecute();

    if (result == null) {
      if (m_CheckAtExecutionTime)
	((InputConsumer) m_BaseActor).input(m_InputToken);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    return ((OutputProducer) m_BaseActor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return ((OutputProducer) m_BaseActor).hasPendingOutput();
  }
}
