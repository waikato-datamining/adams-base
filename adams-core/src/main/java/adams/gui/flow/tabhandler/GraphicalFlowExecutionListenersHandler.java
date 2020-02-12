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
 * GraphicalFlowExecutionListenersHandler.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tabhandler;

import adams.flow.execution.GraphicalFlowExecutionListener;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tab.GraphicalFlowExecutionListenersTab;

import javax.swing.SwingUtilities;
import java.util.HashSet;
import java.util.Set;

/**
 * For managing registered flow execution listeners.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GraphicalFlowExecutionListenersHandler
  extends AbstractTabHandler {

  private static final long serialVersionUID = -6801903616813756519L;

  /** the registered panels: class of panel - (name of panel - AbstractDisplay instance). */
  protected Set<GraphicalFlowExecutionListener> m_Displays;

  /**
   * Initializes the tab handler
   *
   * @param owner	the owning panel
   */
  public GraphicalFlowExecutionListenersHandler(FlowPanel owner) {
    super(owner);
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_Displays = new HashSet<>();
  }

  /**
   * Notifies the {@link GraphicalFlowExecutionListenersTab} instance of a change.
   *
   * @param show	whether to show the tab or leave as is
   */
  protected void update(boolean show) {
    Runnable	run;

    run = () -> {
      if (!getEditor().getTabs().isVisible(GraphicalFlowExecutionListenersTab.class) && show)
	getEditor().getTabs().setVisible(GraphicalFlowExecutionListenersTab.class, true, false);
      GraphicalFlowExecutionListenersTab registered = (GraphicalFlowExecutionListenersTab) getEditor().getTabs().getTab(GraphicalFlowExecutionListenersTab.class);
      if (registered != null)
	registered.update();
    };
    SwingUtilities.invokeLater(run);

    // close displays?
    run = () -> {
      if (!hasDisplays())
	getEditor().getTabs().setVisible(GraphicalFlowExecutionListenersTab.class, false, false);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Registers a listener.
   *
   * @param listener	the listener to register
   */
  public void register(GraphicalFlowExecutionListener listener) {
    m_Displays.add(listener);
    // notify panel
    update(true);
  }

  /**
   * Deregisters a listener.
   *
   * @param listener 	the listener to deregister
   */
  public void deregister(GraphicalFlowExecutionListener listener) {
    // notify panel
    if (m_Displays.remove(listener))
      update(false);
  }

  /**
   * Removes all registered displays.
   */
  public void clear() {
    m_Displays.clear();
    // notify panel
    update(false);
  }

  /**
   * Returns all currently registered displays.
   *
   * @return		the displays
   */
  public Set<GraphicalFlowExecutionListener> getDisplays() {
    return m_Displays;
  }

  /**
   * Returns whether there are any registered displays open.
   *
   * @return		true if at least one open
   */
  public boolean hasDisplays() {
    return (m_Displays.size() > 0);
  }

  /**
   * Gets called when the page changes.
   */
  public void display() {
    getEditor().getTabs().setVisible(
      GraphicalFlowExecutionListenersTab.class,
      hasDisplays(),
      false);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    clear();
  }
}
