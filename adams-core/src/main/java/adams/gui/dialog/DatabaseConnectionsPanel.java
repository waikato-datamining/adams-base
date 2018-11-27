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
 * DatabaseConnectionsPanel.java
 * Copyright (C) 2011-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.ClassLister;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel for managing all the database connections.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DatabaseConnectionsPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -4151139612136850913L;

  /** the tabbed pane for the connection panels. */
  protected BaseTabbedPane m_TabbedPane;

  /** the change listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /** the panels. */
  protected List<AbstractDatabaseConnectionPanel> m_Panels;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_ChangeListeners = new HashSet<>();
  }

  /**
   * Initializes the members.
   */
  protected void initGUI() {
    Class[]					classes;
    AbstractDatabaseConnectionPanel		panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // get available panels
    classes = ClassLister.getSingleton().getClasses(AbstractDatabaseConnectionPanel.class);
    m_Panels  = new ArrayList<>();
    for (Class cls: classes) {
      try {
	panel = (AbstractDatabaseConnectionPanel) cls.newInstance();
	panel.addChangeListener((ChangeEvent e) -> notifyChangeListeners());
	m_Panels.add(panel);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate '" + cls + "':");
	e.printStackTrace();
      }
    }
    Collections.sort(m_Panels);

    // add available panels
    for (AbstractDatabaseConnectionPanel pnl: m_Panels)
      m_TabbedPane.addTab(pnl.getTitle(), pnl);
  }

  /**
   * Adds the listener for changes in the connection.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes in the connection.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners about a change in the connection.
   */
  protected void notifyChangeListeners() {
    ChangeEvent	event;

    event = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(event);
  }

  /**
   * Clears the connections.
   */
  public void disconnectConnections() {
    for (AbstractDatabaseConnectionPanel panel: m_Panels)
      panel.disconnectConnections();
  }
}
