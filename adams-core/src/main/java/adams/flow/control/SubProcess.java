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
 * SubProcess.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;


import adams.flow.core.Actor;
import adams.flow.core.ActorWithConditionalEquivalent;
import adams.flow.core.ActorWithTimedEquivalent;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Encapsulates a sequence of flow items. The first actor must accept input and the last one must produce output.
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
 * &nbsp;&nbsp;&nbsp;default: SubProcess
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubProcess
  extends Sequence
  implements OutputProducer, PauseStateHandler, ActorWithConditionalEquivalent,
             ActorWithTimedEquivalent {

  /** for serialization. */
  private static final long serialVersionUID = 7433940498896052594L;

  /**
   * A specialized director for the SubProcess actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SubProcessDirector
    extends SequentialDirector {

    /** for serialization. */
    private static final long serialVersionUID = 1600945233224761728L;

    /**
     * Sets the group to execute.
     *
     * @param value 	the group
     */
    public void setControlActor(AbstractDirectedControlActor value) {
      if ((value instanceof SubProcess) || (value == null))
	super.setControlActor(value);
      else
	System.err.println(
	    "Group must be a SubProcess actor (provided: "
	    + ((value != null) ? value.getClass().getName() : "-null-") + ")!");
    }

    /**
     * Returns whether the final output of actors is recorded.
     *
     * @return		true
     */
    @Override
    protected boolean isFinalOutputRecorded() {
      return true;
    }

    /**
     * Peforms the execution of the actors.
     *
     * @param startActor	the actor to start with
     * @return		null if everything ok, otherwise the error message
     */
    @Override
    protected String doExecuteActors(Actor startActor) {
      String		result;
      int		i;

      result = super.doExecuteActors(startActor);

      if (result == null) {
	for (i = 0; i < m_FinalOutput.size(); i++)
	  ((SubProcess) getControlActor()).addOutputToken(m_FinalOutput.get(i));
      }

      return result;
    }
  }

  /** for storing generated output tokens. */
  protected transient List<Token> m_OutputTokens;

  /** whether to allow no sub-actors. */
  protected boolean m_AllowEmpty;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Encapsulates a sequence of flow items. The first actor must accept "
      + "input and the last one must produce output.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_AllowEmpty = false;
  }
  
  /**
   * Returns the class that is the corresponding conditional equivalent.
   * 
   * @return		the class, null if none available
   */
  public Class getConditionalEquivalent() {
    return ConditionalSubProcess.class;
  }

  /**
   * Returns the class that is the corresponding timed equivalent.
   * 
   * @return		the class, null if none available
   */
  public Class getTimedEquivalent() {
    return TimedSubProcess.class;
  }

  /**
   * Initializes m_OutputTokens if necessary and returns it.
   *
   * @return		m_OutputTokens
   */
  protected List<Token> getOutputTokens() {
    if (m_OutputTokens == null)
      m_OutputTokens = new ArrayList<Token>();
    return m_OutputTokens;
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected SequentialDirector newDirector() {
    return new SubProcessDirector();
  }

  /**
   * Returns the pause state manager.
   * 
   * @return		the manager
   */
  public PauseStateManager getPauseStateManager() {
    return m_PauseStateManager;
  }

  /**
   * Sets whether we can have no active sub-actors.
   * 
   * @param value	true if to allow no active sub-actors
   */
  public void setAllowEmpty(boolean value) {
    m_AllowEmpty = value;
  }
  
  /**
   * Returns whether it is possible to have no active sub-actors.
   * 
   * @return		true if no active sub-actors allowed
   */
  public boolean getAllowEmpty() {
    return m_AllowEmpty;
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    // sub-process is used sometimes as standalone actor with no outer Flow
    // hence we need to instantiate our own pause state manager
    if (m_PauseStateManager == null)
      m_PauseStateManager = new PauseStateManager();
    
    if (!m_AllowEmpty) {
      if (result == null) {
	if (active() == 0)
	  result = "No active (= non-skipped) actors!";

	if (result == null) {
	  if (!(firstActive() instanceof InputConsumer))
	    result = "First actor ('" + firstActive().getName() + "') does not accept input!";
	  else if (!(lastActive() instanceof OutputProducer))
	    result = "Last actor ('" + lastActive().getName() + "') does not generate output!";
	}
      }
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    if (active() > 0)
      return ((InputConsumer) firstActive()).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (active() > 0)
      return ((OutputProducer) lastActive()).generates();
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
    super.input(token);
    if (m_OutputTokens != null)
      m_OutputTokens.clear();
  }
  
  /**
   * Adds the given token to the list of available output tokens.
   *
   * @param output	the token to add
   * @see		#getOutputTokens()
   */
  protected void addOutputToken(Token output) {
    getOutputTokens().add(output);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    if (m_OutputTokens != null)
      m_OutputTokens.clear();

    result = super.execute();
    
    if (m_Skip)
      getOutputTokens().add(m_CurrentToken);
    
    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    if (getOutputTokens().size() > 0) {
      result = getOutputTokens().get(0);
      getOutputTokens().remove(0);
    }
    else {
      result = null;
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (getOutputTokens().size() > 0);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_OutputTokens != null)
      m_OutputTokens.clear();
  }
}
