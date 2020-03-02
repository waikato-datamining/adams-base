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
 * RegisteredDisplaysTab.java
 * Copyright (C) 2014-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import adams.flow.core.AbstractDisplay;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.DetachablePanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tabhandler.RegisteredDisplaysHandler;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays the registered displays for a particular class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RegisteredDisplaysTab
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
    return "Displays";
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

      RegisteredDisplaysHandler handler = panel.getTabHandler(RegisteredDisplaysHandler.class);
      if (handler == null)
        return;
      Map<Class,HashMap<String,AbstractDisplay>> registered = handler.getDisplays();
      if (registered.size() == 0)
	return;

      m_TabbedDisplays = new BaseTabbedPane(BaseTabbedPane.BOTTOM);
      m_TabbedDisplays.setDetachableTabs(true);
      add(m_TabbedDisplays, BorderLayout.CENTER);
      for (Class regCls: registered.keySet()) {
	HashMap<String,AbstractDisplay> displays = registered.get(regCls);
	if (displays.size() == 0)
	  continue;
	BaseTabbedPane tabbedDisplays = new BaseTabbedPane(BaseTabbedPane.TOP);
	for (String name: displays.keySet()) {
	  AbstractDisplay display = displays.get(name);
	  String title   = name;
	  if (display.getParentComponent() instanceof FlowPanel)
	    title = ((FlowPanel) display.getParentComponent()).getTitle() + ":" + title;
	  tabbedDisplays.addTab(title, display.getPanel());
	}
	String title = "Type:" + regCls.getSimpleName();
	BasePanel bpanel = new BasePanel(new BorderLayout());
	bpanel.add(tabbedDisplays, BorderLayout.CENTER);
	DetachablePanel detachable = new DetachablePanel();
	detachable.setFrameTitle(title);
	detachable.getContentPanel().add(bpanel, BorderLayout.CENTER);
	m_TabbedDisplays.addTab(title, detachable);
      }

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
