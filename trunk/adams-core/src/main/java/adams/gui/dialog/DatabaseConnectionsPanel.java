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
 * DatabaseConnectionsPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;

/**
 * Panel for managing all the database connections.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatabaseConnectionsPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -4151139612136850913L;

  /** the tabbed pane for the connection panels. */
  protected BaseTabbedPane m_TabbedPane;

  /** the change listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_ChangeListeners = new HashSet<ChangeListener>();
  }

  /**
   * Initializes the members.
   */
  protected void initGUI() {
    String[]					classes;
    Vector<AbstractDatabaseConnectionPanel> 	panels;
    AbstractDatabaseConnectionPanel		panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // get available panels
    classes = AbstractDatabaseConnectionPanel.getPanels();
    panels  = new Vector<AbstractDatabaseConnectionPanel>();
    for (String cls: classes) {
      try {
	panel = (AbstractDatabaseConnectionPanel) Class.forName(cls).newInstance();
	panel.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent e) {
	    notifyChangeListeners();
	  }
	});
	panels.add(panel);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate '" + cls + "':");
	e.printStackTrace();
      }
    }
    Collections.sort(panels);

    // add available panels
    for (AbstractDatabaseConnectionPanel pnl: panels)
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
}
