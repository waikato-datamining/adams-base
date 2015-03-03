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
 * SequenceSource.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;
import java.util.List;

import adams.flow.control.AbstractDirectedControlActor;
import adams.flow.control.MutableConnectedControlActor;
import adams.flow.control.SequentialDirector;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Encapsulates a sequence of flow items, with the last one generating the output for this meta-source.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SequenceSource
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;All the actors that define this sequence.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequenceSource
  extends MutableConnectedControlActor
  implements OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -1007878227244351146L;

  /**
   * A specialized director for the SequenceSource actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SequenceSourceDirector
    extends SequentialDirector {

    /** for serialization. */
    private static final long serialVersionUID = 1600945233224761728L;

    /**
     * Sets the group to execute.
     *
     * @param value 	the group
     */
    public void setControlActor(AbstractDirectedControlActor value) {
      if ((value instanceof SequenceSource) || (value == null))
	super.setControlActor(value);
      else
	System.err.println(
	    "Group must be a SequenceSource actor (provided: "
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
    protected String doExecuteActors(AbstractActor startActor) {
      String		result;
      int		i;

      result = super.doExecuteActors(startActor);

      if (result == null) {
	for (i = 0; i < m_FinalOutput.size(); i++)
	  ((SequenceSource) getControlActor()).addOutputToken(m_FinalOutput.get(i));
      }

      return result;
    }
  }

  /** for storing generated output tokens. */
  protected transient List<Token> m_OutputTokens;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Encapsulates a sequence of flow items, with the last one generating "
      + "the output for this meta-source.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "All the actors that define this sequence.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, ActorExecution.SEQUENTIAL, false);
  }

  /**
   * Initializes m_OutputTokens if necessary and returns it.
   *
   * @return		m_OutputTokens
   */
  public List<Token> getOutputTokens() {
    if (m_OutputTokens == null)
      m_OutputTokens = new ArrayList<Token>();
    return m_OutputTokens;
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
   * Returns an instance of a director.
   *
   * @return		the director
   */
  @Override
  protected SequentialDirector newDirector() {
    return new SequenceSourceDirector();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] generates() {
    Class[]		result;
    AbstractActor	last;

    result = new Class[0];

    last = lastActive();
    if ((last != null) && (last instanceof OutputProducer))
      result = ((OutputProducer) last).generates();

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
   * <p/>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
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
