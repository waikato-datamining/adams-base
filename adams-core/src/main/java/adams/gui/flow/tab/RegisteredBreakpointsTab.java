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
 * RegisteredBreakpointsTab.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import java.awt.BorderLayout;
import java.util.HashMap;

import adams.flow.control.Breakpoint;
import adams.gui.core.BaseTabbedPane;
import adams.gui.flow.FlowPanel;

/**
 * Displays the registered panels for a particular class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegisteredBreakpointsTab
  extends AbstractTabChangeAwareEditorTab {

  /** for serialization. */
  private static final long serialVersionUID = 3636125950515045125L;
  
  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Breakpoints";
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
  public synchronized void flowPanelChanged(FlowPanel panel) {
    HashMap<String,Breakpoint>	panels;
    BaseTabbedPane		tabbedPanels;
    Breakpoint			breakpoint;
    String			title;
    
    removeAll();
    setLayout(new BorderLayout());
    if (getParent() != null) {
      getParent().invalidate();
      getParent().doLayout();
      getParent().repaint();
    }
    
    if (panel == null)
      return;

    panels = panel.getRegisteredBreakpoints();
    if (panels.size() == 0)
      return;
    
    tabbedPanels = new BaseTabbedPane(BaseTabbedPane.TOP);
    for (String name: panels.keySet()) {
      breakpoint = panels.get(name);
      title      = name;
      if (breakpoint.getParentComponent() instanceof FlowPanel)
	title = ((FlowPanel) breakpoint.getParentComponent()).getTitle() + ":" + title;
      tabbedPanels.addTab(title, breakpoint.getPanel());
    }
    add(tabbedPanels, BorderLayout.CENTER);
      
    if (getParent() != null) {
      getParent().invalidate();
      getParent().doLayout();
      getParent().repaint();
    }
  }
}
