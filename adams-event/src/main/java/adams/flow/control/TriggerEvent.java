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
 * TriggerEvent.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.core.AbstractActor;
import adams.flow.core.ControlActor;
import adams.flow.core.EventHelper;
import adams.flow.core.TriggerableEventReference;
import adams.flow.core.Unknown;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Triggers the specified event.
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
 * &nbsp;&nbsp;&nbsp;default: TriggerEvent
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
 * <pre>-event &lt;adams.flow.core.EventReference&gt; (property: event)
 * &nbsp;&nbsp;&nbsp;The name of the event to trigger.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TriggerEvent
  extends AbstractTransformer
  implements ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = -6090673233883296452L;

  /** the event to trigger. */
  protected TriggerableEventReference m_Event;

  /** the event actor to execute. */
  protected AbstractActor m_EventActor;

  /** the helper class. */
  protected EventHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Triggers the specified event.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "event", "event",
	    new TriggerableEventReference());
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_EventActor = null;
    m_Helper     = new EventHelper();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "event", m_Event.getValue());
  }

  /**
   * Sets the name of the event to trigger.
   *
   * @param value	the name
   */
  public void setEvent(TriggerableEventReference value) {
    m_Event = value;
    reset();
  }

  /**
   * Returns the name of the event to trigger.
   *
   * @return		the name
   */
  public TriggerableEventReference getEvent() {
    return m_Event;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String eventTipText() {
    return "The name of the event to trigger.";
  }

  /**
   * Tries to find the event referenced by its global name.
   *
   * @return		the event or null if not found
   */
  protected AbstractActor findEventActor() {
    return m_Helper.findEventRecursive(this, getEvent());
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      m_EventActor = findEventActor();
      if (m_EventActor == null)
        result = "Couldn't find event actor '" + getEvent() + "'!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    m_OutputToken = m_InputToken;
    
    if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
      getFlowExecutionListeningSupporter().getFlowExecutionListener().preExecute(m_EventActor);
    result = m_EventActor.execute();
    if (getFlowExecutionListeningSupporter().isFlowExecutionListeningEnabled())
      getFlowExecutionListeningSupporter().getFlowExecutionListener().postExecute(m_EventActor);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_EventActor != null)
      m_EventActor.stopExecution();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    if (m_EventActor != null)
      m_EventActor.wrapUp();

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_EventActor != null)
      m_EventActor.cleanUp();

    super.cleanUp();
  }
}
