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
 * TabVisibilityChangeEvent.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.event;

import java.awt.Component;
import java.util.EventObject;

import adams.gui.core.BaseTabbedPaneWithTabHiding;

/**
 * Event that gets sent when tabs get hidden or set visible in a
 * {@link BaseTabbedPaneWithTabHiding}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see BaseTabbedPaneWithTabHiding
 */
public class TabVisibilityChangeEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 2987454130978038905L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** hide event. */
    HIDE,
    /** display event. */
    DISPLAY
  }

  /** the type of event. */
  protected Type m_Type;

  /** the component of the tab that was made visible or got hidden. */
  protected Component m_Component;

  /**
   * Initializes the event.
   *
   * @param source	the tabbed pane that triggered the event
   * @param type	the type of event
   * @param component	the component of the tab
   */
  public TabVisibilityChangeEvent(BaseTabbedPaneWithTabHiding source, Type type, Component component) {
    super(source);

    m_Type      = type;
    m_Component = component;
  }

  /**
   * Returns the tabbed pane that triggered the event.
   *
   * @return		the tabbed pane
   */
  public BaseTabbedPaneWithTabHiding getTabbedPane() {
    return (BaseTabbedPaneWithTabHiding) getSource();
  }

  /**
   * Returns the type of event.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the component of the tab that was hidden or made visible.
   *
   * @return		the component
   */
  public Component getComponent() {
    return m_Component;
  }
}
