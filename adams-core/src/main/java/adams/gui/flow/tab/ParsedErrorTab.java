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
 * ParsedErrorTab.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tab;

import adams.gui.core.BaseTabbedPane;
import adams.gui.event.TabClosedEvent;
import adams.gui.flow.tabhandler.ParsedErrorHandler;
import adams.gui.flow.tabhandler.ParsedErrorHandler.ParseOutput;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

/**
 * For displaying the panels for parsed errors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ParsedErrorTab
  extends AbstractEditorTab {

  private static final long serialVersionUID = 9136385384294940943L;

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Parsed errors";
  }

  /**
   * Updates the panels.
   */
  public void update() {
    Runnable	run;

    run = () -> {
      removeAll();
      setLayout(new BorderLayout());
      if (getParent() != null) {
	getParent().invalidate();
	getParent().doLayout();
	getParent().repaint();
      }

      if (!m_Owner.getOwner().hasCurrentPanel())
	return;

      ParsedErrorHandler handler = m_Owner.getOwner().getCurrentPanel().getTabHandler(ParsedErrorHandler.class);
      if (handler == null)
	return;
      if (!handler.hasOutputs())
	return;

      final BaseTabbedPane tabbed = new BaseTabbedPane();
      tabbed.setShowCloseTabButton(true);
      for (ParseOutput output: handler.getOutputs())
	tabbed.addTab(output.getTitle(), output.getPane());
      tabbed.setSelectedIndex(handler.getOutputs().size() - 1);
      tabbed.addTabClosedListeners((TabClosedEvent e) -> {
	handler.remove(e.getComponent());
	// close tab if no outputs left
	if (!handler.hasOutputs()) {
	  tabbed.clearTabClosedListeners();
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
