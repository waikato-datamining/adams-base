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
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import adams.flow.core.AbstractActor;
import adams.flow.core.AbstractDisplay;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.DetachablePanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tabhandler.RegisteredDisplaysHandler;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
   * Groups the registered displays by title.
   *
   * @param displays	the displays to group
   * @return		the grouped displays
   */
  protected Map<String,List<AbstractDisplay>> groupByTitle(Map<Class,HashMap<String,AbstractDisplay>> displays) {
    Map<String,List<AbstractDisplay>>	result;
    HashMap<String,AbstractDisplay>	subset;
    String				title;

    result = new HashMap<>();

    // group by flow title
    for (Class cls: displays.keySet()) {
      subset = displays.get(cls);
      for (String key : subset.keySet()) {
	if (subset.get(key).getParentComponent() != null) {
	  title = ((FlowPanel) subset.get(key).getParentComponent()).getTitle();
	  if (!result.containsKey(title))
	    result.put(title, new ArrayList<>());
	  result.get(title).add(subset.get(key));
	}
      }
    }

    // sort the subgroups
    for (String t: result.keySet())
      result.get(t).sort(Comparator.comparing(AbstractActor::getName));

    return result;
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
      if (registered.isEmpty())
	return;

      // sort titles
      Map<String,List<AbstractDisplay>> grouped = groupByTitle(registered);
      List<String> titles = new ArrayList<>(grouped.keySet());
      Collections.sort(titles);
      if (grouped.isEmpty())
	return;

      m_TabbedDisplays = new BaseTabbedPane(BaseTabbedPane.TOP);
      m_TabbedDisplays.setDetachableTabs(true);
      add(m_TabbedDisplays, BorderLayout.CENTER);
      for (String title: titles) {
	BaseTabbedPane tabbedDisplays = new BaseTabbedPane(BaseTabbedPane.BOTTOM);
	for (AbstractDisplay display: grouped.get(title))
	  tabbedDisplays.addTab(display.getName(), display.getPanel());
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
