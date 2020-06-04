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
 * GraphicalFlowExecutionListenersTab.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import adams.flow.execution.GraphicalFlowExecutionListener;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.DetachablePanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tabhandler.GraphicalFlowExecutionListenersHandler;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.Set;

/**
 * Displays the registered flow execution listeners.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GraphicalFlowExecutionListenersTab
  extends AbstractTabChangeAwareEditorTab
  implements RuntimeTab {

  /** for serialization. */
  private static final long serialVersionUID = 3636125950515045125L;

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedDisplays;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Listeners";
  }

  /**
   * Updates the panels.
   */
  public void update() {
    flowPanelChanged(getCurrentPanel());
  }
  
  /**
   * Notifies the tab of the currently selected flow panel.
   *
   * @param panel	the new panel
   */
  @Override
  public void flowPanelChanged(FlowPanel panel) {
    Runnable	run;

    run = () -> {
      removeAll();
      setLayout(new BorderLayout());
      if (getParent() != null) {
	getParent().invalidate();
	getParent().doLayout();
	getParent().repaint();
      }

      if (panel == null)
	return;

      GraphicalFlowExecutionListenersHandler handler = panel.getTabHandler(GraphicalFlowExecutionListenersHandler.class);
      if (handler == null)
        return;
      Set<GraphicalFlowExecutionListener> registered = handler.getDisplays();
      if (registered.size() == 0)
	return;

	m_TabbedDisplays = new BaseTabbedPane(BaseTabbedPane.TOP);
	m_TabbedDisplays.setDetachableTabs(true);
	for (GraphicalFlowExecutionListener listener: registered) {
	  String title = listener.getListenerTitle();
	  if (listener.getOwner() != null) {
	    if (listener.getOwner().getParentComponent() instanceof FlowPanel)
	      title = ((FlowPanel) listener.getOwner().getParentComponent()).getTitle() + ":" + title;
	  }
	  BasePanel listenerPanel = listener.newListenerPanelIfNecessary();
	  DetachablePanel detachable = new DetachablePanel();
	  detachable.setFrameTitle(title);
	  detachable.getContentPanel().add(listenerPanel, BorderLayout.CENTER);
	  m_TabbedDisplays.addTab(title, detachable);
	}
	add(m_TabbedDisplays, BorderLayout.CENTER);

      if (getParent() != null) {
	getParent().invalidate();
	getParent().doLayout();
	getParent().repaint();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the tabbed displays.
   *
   * @return		the pane, null if not available
   */
  public BaseTabbedPane getTabbedDisplays() {
    return m_TabbedDisplays;
  }
}
