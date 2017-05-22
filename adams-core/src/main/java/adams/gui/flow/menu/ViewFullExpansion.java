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

/**
 * ViewFullExpansion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.option.DebugNestedProducer;
import adams.core.option.NestedConsumer;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.flow.FlowPanel;

import java.awt.event.ActionEvent;

/**
 * Fully expands the flow and displays it in a new tab.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ViewFullExpansion
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  public ViewFullExpansion() {
    super();
    setAsynchronous(true);
  }

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Full expansion...";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    DebugNestedProducer		producer;
    NestedConsumer 		consumer;
    Actor			actor;
    Actor			expanded;
    FlowPanel			panel;
    String			title;
    FlowPanel			panelCopy;


    panel = m_State.getCurrentPanel();
    m_State.showStatus("Expanding: " + panel.getTitle() + "...");

    title    = "Expanded: " + panel.getTitle();
    consumer = new NestedConsumer();
    producer = new DebugNestedProducer();
    producer.setOutputVariableValues(false);
    actor = m_State.getCurrentFlow();
    actor.setUp();
    consumer.setInput(producer.produce(actor));
    expanded = (Actor) consumer.consume();
    panelCopy = m_State.getFlowPanels().newPanel();
    panelCopy.setCurrentFlow(expanded);
    panelCopy.setTitle(title);
    panelCopy.updateTitle();
    panelCopy.setDebugSourcePanel(panel);
    if (expanded instanceof Flow)
      ((Flow) expanded).setParentComponent(panelCopy);

    m_State.showStatus("");
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(
	   m_State.hasCurrentPanel()
	&& !m_State.isSwingWorkerRunning());
  }
}
