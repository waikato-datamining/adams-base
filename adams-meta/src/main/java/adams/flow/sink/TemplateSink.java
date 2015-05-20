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
 * TemplateSink.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.Hashtable;

import adams.flow.core.AbstractTemplate;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.template.AbstractActorTemplate;
import adams.flow.template.DummySink;

/**
 <!-- globalinfo-start -->
 * Lets a sink generated from a template consume the input tokens.
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
 * &nbsp;&nbsp;&nbsp;default: TemplateSink
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
 * &nbsp;&nbsp;&nbsp;default: adams.flow.template.DummySink
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TemplateSink
  extends AbstractTemplate
  implements InputConsumer {

  /** for serialization. */
  private static final long serialVersionUID = -1017031413702121257L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENT = "current";

  /** the token that is to be fed to the global sink. */
  protected transient Token m_CurrentInput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Lets a sink generated from a template consume the input tokens.";
  }

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_CurrentInput = null;
  }

  /**
   * Returns the default template to use.
   * 
   * @return		the template
   */
  protected AbstractActorTemplate getDefaultTemplate() {
    return new DummySink();
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_CURRENT, m_CurrentInput);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENT)) {
      m_CurrentInput = (Token) state.get(BACKUP_CURRENT);
      state.remove(BACKUP_CURRENT);
    }

    super.restoreState(state);
  }

  /**
   * Initializes the template for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpTemplate() {
    String		result;
   
    result = super.setUpTemplate();
    
    if (result == null) {
      if (!ActorUtils.isSink(m_Actor))
	result = "Template '" + m_Template + "' does not generate a sink actor: " + m_Actor.getClass().getName();
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
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
  public void input(Token token) {
    m_CurrentInput = token;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    String		result;

    result = null;
    
    if (m_Actor == null)
      result = setUpTemplate();
    
    if (result == null) {
      ((InputConsumer) m_Actor).input(m_CurrentInput);
      result = m_Actor.execute();
    }
    
    return result;
  }
}
