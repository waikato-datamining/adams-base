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
 * RegisteredDisplaysTab.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import java.awt.BorderLayout;
import java.util.HashMap;

import adams.flow.core.AbstractDisplay;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.flow.FlowPanel;

/**
 * Displays the registered displays for a particular class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegisteredDisplaysTab
  extends AbstractTabChangeAwareEditorTab
  implements RuntimeTab {

  /** for serialization. */
  private static final long serialVersionUID = 3636125950515045125L;
  
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
  public synchronized void flowPanelChanged(FlowPanel panel) {
    HashMap<Class,HashMap<String,AbstractDisplay>>	registered;
    HashMap<String,AbstractDisplay>			displays;
    BaseTabbedPane					tabbedCls;
    BaseTabbedPane					tabbedDisplays;
    BasePanel						bpanel;
    AbstractDisplay					display;
    String						title;
    
    removeAll();
    setLayout(new BorderLayout());
    if (getParent() != null) {
      getParent().invalidate();
      getParent().doLayout();
      getParent().repaint();
    }
    
    if (panel == null)
      return;
    
    registered = panel.getRegisteredDisplays();
    if (registered.size() == 0)
      return;
    
    tabbedCls = new BaseTabbedPane(BaseTabbedPane.BOTTOM);
    add(tabbedCls, BorderLayout.CENTER);
    for (Class regCls: registered.keySet()) {
      displays = registered.get(regCls);
      if (displays.size() == 0)
	continue;
      tabbedDisplays = new BaseTabbedPane(BaseTabbedPane.TOP);
      for (String name: displays.keySet()) {
	display = displays.get(name);
	title   = name;
	if (display.getParentComponent() instanceof FlowPanel)
	  title = ((FlowPanel) display.getParentComponent()).getTitle() + ":" + title;
	tabbedDisplays.addTab(title, display.getPanel());
      }
      bpanel = new BasePanel(new BorderLayout());
      bpanel.add(tabbedDisplays, BorderLayout.CENTER);
      tabbedCls.addTab("Type:" + regCls.getSimpleName(), bpanel);
    }

    if (getParent() != null) {
      getParent().invalidate();
      getParent().doLayout();
      getParent().repaint();
    }
  }
}
