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
 * MultiTrigger.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.trigger;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.RestartHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages multiple triggers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiTrigger
  extends AbstractTrigger {

  private static final long serialVersionUID = -1011230298891411662L;

  /** the triggers to manage. */
  protected List<AbstractTrigger> m_Triggers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages multiple triggers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "trigger", "triggers",
      new AbstractTrigger[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Triggers = new ArrayList<>();
  }

  /**
   * Appends the specified trigger.
   *
   * @param value	the trigger to add
   */
  public void addTrigger(AbstractTrigger value) {
    m_Triggers.add(value);
  }

  /**
   * Sets the managed triggers.
   *
   * @param value	the triggers
   */
  public void setTriggers(AbstractTrigger[] value) {
    m_Triggers.clear();
    m_Triggers.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the managed triggers.
   *
   * @return		the trigger
   */
  public AbstractTrigger[] getTriggers() {
    return m_Triggers.toArray(new AbstractTrigger[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String triggersTipText() {
    return "The managed triggers.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "triggers", m_Triggers, "trigger(s): ");
  }

  /**
   * Sets the restart handler to use.
   *
   * @param value 	the handler to use
   */
  public void setRestartHandler(RestartHandler value) {
    super.setRestartHandler(value);
    for (AbstractTrigger trigger: m_Triggers)
      trigger.setRestartHandler(value);
  }

  /**
   * Starts the trigger.
   *
   * @param flow	the flow to handle
   * @return		null if successfully started, otherwise error message
   */
  @Override
  protected String doStart(Flow flow) {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < m_Triggers.size(); i++) {
      if (isLoggingEnabled())
        getLogger().info("Starting trigger #" + (i+1) + ": " + m_Triggers.get(i));
      result = m_Triggers.get(i).start(flow);
      if (result != null) {
        getLogger().severe("Trigger #" + (i+1) + " failed to start: " + result);
        stop();
        break;
      }
    }

    return result;
  }

  /**
   * Stops the trigger.
   *
   * @return		null if successfully stopped, otherwise error message
   */
  @Override
  public String stop() {
    MessageCollection	errors;
    String 		msg;
    int			i;

    errors = new MessageCollection();
    for (i = 0; i < m_Triggers.size(); i++) {
      if (isLoggingEnabled())
        getLogger().info("Stopping trigger #" + (i+1) + ": " + m_Triggers.get(i));
      msg = m_Triggers.get(i).stop();
      if (msg != null) {
        errors.add(msg);
        getLogger().severe("Trigger #" + (i+1) + " failed to stop: " + msg);
      }
    }

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }
}
