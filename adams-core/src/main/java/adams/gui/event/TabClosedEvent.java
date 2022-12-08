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
 * TabClosedEvent.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.core.BaseTabbedPane;

import java.awt.Component;
import java.util.EventObject;

/**
 * Event that gets sent when a tab gets closed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TabClosedEvent
  extends EventObject {

  private static final long serialVersionUID = -6446664500080231501L;

  /** the index of the removed tab. */
  protected int m_TabIndex;

  /** the component that got removed. */
  protected Component m_Component;

  /**
   * Constructs a prototypical Event.
   *
   * @param tabbedPane 	the tabbed pane that sent the event
   * @param tabIndex 	the index of the tab that got removed
   * @param component 	the component that got removed with the tab
   * @throws IllegalArgumentException if source is null
   */
  public TabClosedEvent(BaseTabbedPane tabbedPane, int tabIndex, Component component) {
    super(tabbedPane);
    m_TabIndex  = tabIndex;
    m_Component = component;
  }

  /**
   * Returns the tabbed pane that triggered the event.
   *
   * @return		the pane
   */
  public BaseTabbedPane getTabbedPane() {
    return (BaseTabbedPane) getSource();
  }

  /**
   * Returns the index of the removed tab.
   *
   * @return		the index
   */
  public int getTabIndex() {
    return m_TabIndex;
  }

  /**
   * Returns the component that got removed.
   *
   * @return		the component
   */
  public Component getComponent() {
    return m_Component;
  }
}
