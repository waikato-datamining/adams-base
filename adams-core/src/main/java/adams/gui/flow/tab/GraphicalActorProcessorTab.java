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
 * GraphicalActorProcessorTab.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ErrorMessagePanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tabhandler.GraphicalActorProcessorHandler;
import adams.gui.flow.tabhandler.GraphicalActorProcessorHandler.Output;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For displaying the graphical output from actor processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GraphicalActorProcessorTab
  extends AbstractTabChangeAwareEditorTab
  implements RuntimeTab {

  private static final long serialVersionUID = 9136385384294940943L;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Actor processors";
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

      GraphicalActorProcessorHandler handler = panel.getTabHandler(GraphicalActorProcessorHandler.class);
      if (handler == null)
        return;
      if (!handler.hasOutputs())
	return;

      final BaseTabbedPane tabbed = new BaseTabbedPane();
      tabbed.setShowCloseTabButton(true);
      for (Output output: handler.getOutputs()) {
	BaseSplitPane splitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
	splitPane.setResizeWeight(0.5);
        if (output.hasError()) {
          splitPane.setTopComponent(output.component);
	  ErrorMessagePanel errorPanel = new ErrorMessagePanel();
	  errorPanel.setErrorMessage(output.error);
          splitPane.setBottomComponent(errorPanel);
	}
	else {
          splitPane.setTopComponent(output.component);
          splitPane.setBottomComponentHidden(true);
	}
	tabbed.addTab(output.title, splitPane);
      }
      tabbed.setSelectedIndex(handler.getOutputs().size() - 1);
      tabbed.addTabChangeListener((ChangeEvent e) -> {
        // remove components that are no longer present
	Set<Component> comps = new HashSet<>();
	for (int i = 0; i < tabbed.getTabCount(); i++) {
	  BaseSplitPane split = (BaseSplitPane) tabbed.getComponentAt(i);
	  comps.add(split.getTopComponent());
	}

	List<Output> remove = new ArrayList<>();
	for (Output output: handler.getOutputs()) {
	  if (!comps.contains(output.component))
	    remove.add(output);
	}

	for (Output output: remove)
	  handler.remove(output.component);

	// close tab if no outputs left
	if (!handler.hasOutputs()) {
	  tabbed.clearTabChangeListeners();
	  handler.display();
	}
      });
      add(tabbed, BorderLayout.CENTER);

      if (getParent() != null) {
	getParent().invalidate();
	getParent().doLayout();
	getParent().repaint();
      }
    };
    SwingUtilities.invokeLater(run);
  }
}
