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
 * VariableChangedEvent.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.VariableNameNoUpdate;
import adams.event.VariableChangeEvent;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Listens to a any changes to the specified variable.<br>
 * This allows, for instance, the monitoring of a variable.<br>
 * Enable the 'noDiscard' property to process all change events - NB: this can slow down the system significantly.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: VariableChangedEvent
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
 * <pre>-variable &lt;adams.core.VariableNameNoUpdate&gt; (property: variable)
 * &nbsp;&nbsp;&nbsp;The variable to monitor for changes.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 * <pre>-no-discard &lt;boolean&gt; (property: noDiscard)
 * &nbsp;&nbsp;&nbsp;If enabled, no change event gets discarded; CAUTION: enabling this option 
 * &nbsp;&nbsp;&nbsp;can slow down the system significantly.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to execute in case of a change event.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * For more information on the schedule format, see
 * <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html" target="_blank">CronTrigger Tutorial</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableChangedEvent
  extends AbstractMutableActorDaemonEvent<VariableChangeEvent, Object> {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the variable to listen to. */
  protected VariableNameNoUpdate m_Variable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Listens to a any changes to the specified variable.\n"
        + "This allows, for instance, the monitoring of a variable.\n"
        + "Enable the 'noDiscard' property to process all change events - NB: this "
	+ "can slow down the system significantly.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "variable", "variable",
	    new VariableNameNoUpdate());
  }

  /**
   * Sets the variable to monitor.
   *
   * @param value 	the variable
   */
  public void setVariable(VariableNameNoUpdate value) {
    m_Variable = value;
    reset();
  }

  /**
   * Returns the variable to monitor
   *
   * @return 		the variable
   */
  public VariableNameNoUpdate getVariable() {
    return m_Variable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableTipText() {
    return "The variable to monitor for changes.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "variable", m_Variable, "var: ");
    value   = QuickInfoHelper.toString(this, "noDiscard", m_NoDiscard, "no discard", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Checks the actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkActors(AbstractActor[] actors) {
    int		i;

    for (i = 0; i < actors.length; i++) {
      if (actors[i].getSkip())
	continue;
      if (ActorUtils.isStandalone(actors[i]))
	continue;
      if (!ActorUtils.isSource(actors[i]))
	return "Actor #" + (i+1) + " does not produce any output!";
      break;
    }

    return null;
  }

  /**
   * Checks whether the event is being handled.
   *
   * @param e		the event to check
   * @return		true if being handled
   */
  @Override
  protected boolean handlesEvent(VariableChangeEvent e) {
    return true;
  }

  /**
   * Preprocesses the event.
   *
   * @param e		the event to preprocess
   * @return		the output of the preprocessing
   */
  @Override
  protected Object preProcessEvent(VariableChangeEvent e) {
    return e;
  }

  /**
   * Returns whether the preprocessed event is used as input token.
   *
   * @return		always false
   */
  @Override
  protected boolean usePreProcessedAsInput() {
    return false;
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    if (e.getName().equals(m_Variable.getValue()))
      processEvent(e);
  }
}
