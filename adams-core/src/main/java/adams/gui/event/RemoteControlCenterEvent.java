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
 * RemoteControlCenterEvent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.event;

import adams.gui.tools.remotecontrolcenter.RemoteControlCenterPanel;
import adams.gui.tools.remotecontrolcenter.panels.AbstractRemoteControlCenterTab;

import java.util.EventObject;

/**
 * Events that the RemoteControlCenter send.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterEvent
  extends EventObject {

  private static final long serialVersionUID = -8645797554948304600L;

  /**
   * The type of event.
   */
  public enum EventType {
    SCRIPTING_ENGINE_UPDATED,
  }

  /** the event type. */
  protected EventType m_Type;

  /** the sub-panel that triggered the event (if any). */
  protected AbstractRemoteControlCenterTab m_SubPanel;

  /**
   * Constructs the event.
   *
   * @param source 	the panel that triggered the event
   * @param type 	the event type
   */
  public RemoteControlCenterEvent(RemoteControlCenterPanel source, EventType type) {
    this(source, null, type);
  }

  /**
   * Constructs the event.
   *
   * @param source 	the panel that triggered the event
   * @param subpanel 	the sub-panel that triggered the event, can be null
   * @param type 	the event type
   */
  public RemoteControlCenterEvent(RemoteControlCenterPanel source, AbstractRemoteControlCenterTab subpanel, EventType type) {
    super(source);

    m_Type     = type;
    m_SubPanel = subpanel;
  }

  /**
   * Returns the panel that triggered the event.
   *
   * @return		the panel
   */
  public RemoteControlCenterPanel getPanel() {
    return (RemoteControlCenterPanel) getSource();
  }

  /**
   * Returns the sub-panel that triggered the event.
   *
   * @return		the panel, can be null
   */
  public AbstractRemoteControlCenterTab getSubPanel() {
    return m_SubPanel;
  }

  /**
   * Returns the event type.
   *
   * @return		the type
   */
  public EventType getType() {
    return m_Type;
  }
}
