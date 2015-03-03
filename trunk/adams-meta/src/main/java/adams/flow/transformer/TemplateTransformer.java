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
 * TemplateTransformer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Hashtable;

import adams.flow.core.AbstractTemplate;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.template.AbstractActorTemplate;
import adams.flow.template.DummyTransformer;

/**
 <!-- globalinfo-start -->
 * Feeds tokens into an actor generated from a template and broadcasts the generated output tokens.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: TemplateTransformer
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
 * <pre>-template &lt;adams.flow.template.AbstractActorTemplate&gt; (property: template)
 * &nbsp;&nbsp;&nbsp;The template to use for generating the actual actor.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.template.DummyTransformer
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TemplateTransformer
  extends AbstractTemplate
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 2327297866200504943L;

  /** the key for storing the current input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the token that is to be fed into the global transformer. */
  protected transient Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Feeds tokens into an actor generated from a template and broadcasts the generated output tokens.";
  }

  /**
   * Returns the default template to use.
   *
   * @return		the template
   */
  @Override
  protected AbstractActorTemplate getDefaultTemplate() {
    return new DummyTransformer();
  }

  /**
   * Initializes the template for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String setUpTemplate() {
    String		result;

    result = super.setUpTemplate();

    if (result == null) {
      if (!ActorUtils.isTransformer(m_Actor))
	result = "Template '" + m_Template + "' does not generate a transformer actor: " + m_Actor.getClass().getName();
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

    result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_InputToken  = null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    if (m_Actor != null)
      return ((InputConsumer) m_Actor).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    m_InputToken  = token;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		depends on the global actor
   */
  @Override
  public Class[] generates() {
    if (m_Actor != null)
      return ((OutputProducer) m_Actor).generates();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;

    result = null;

    if (m_Actor == null)
      result = setUpTemplate();

    if (result == null) {
      ((InputConsumer) m_Actor).input(m_InputToken);
      result = m_Actor.execute();
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    m_InputToken = null;
    return ((OutputProducer) m_Actor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Actor != null) && ((OutputProducer) m_Actor).hasPendingOutput();
  }
}
