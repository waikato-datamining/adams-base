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
 * ExternalTransformer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import adams.flow.core.AbstractExternalActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Transformer that executes an external transformer actor stored on disk.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: ExternalTransformer
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseString&gt; [-annotation ...] (property: annotations)
 *         The annotations to attach to this actor.
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.FlowFile&gt; (property: actorFile)
 *         The file containing the external actor.
 *         default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExternalTransformer
  extends AbstractExternalActor
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 8557732787822426844L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the current input token. */
  protected transient Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Transformer that executes an external transformer actor stored on disk.";
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
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String	result;

    result = super.setUpExternalActor();

    if (result == null) {
      if (!ActorUtils.isTransformer(m_ExternalActor))
	result = "External actor '" + m_ActorFile.getAbsolutePath() + "' is not a transformer!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    if (m_ExternalActor != null)
      return ((InputConsumer) m_ExternalActor).accepts();
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
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_ExternalActor != null)
      return ((OutputProducer) m_ExternalActor).generates();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Gets called in the doExceute() method, after an optional
   * setUpExternalActor() call (in case a variable is used for the actor file),
   * but before the external actor's execute() method is called.
   * <p/>
   * Sets the input token.
   *
   * @return		null if everything ok, otherwise error message
   */
  protected String preExecuteExternalActorHook() {
    ((InputConsumer) m_ExternalActor).input(m_InputToken);
    m_InputToken = null;
    return null;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    return ((OutputProducer) m_ExternalActor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return ((OutputProducer) m_ExternalActor).hasPendingOutput();
  }
}
