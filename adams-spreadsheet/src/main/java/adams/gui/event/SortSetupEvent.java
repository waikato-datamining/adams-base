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
 * SortSetupEvent.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.tools.spreadsheetviewer.SortDefinitionPanel;
import adams.gui.tools.spreadsheetviewer.SortPanel;

import java.util.EventObject;

/**
 * Event that gets sent when the {@link SortPanel} setup changes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortSetupEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -1322917092297786611L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum EventType {
    /** a definition was added. */
    ADD,
    /** a definition was removed. */
    REMOVE,
    /** the setup was modified. */
    MODIFIED,
    /** the setup was reset. */
    RESET,
    /** the setup was moved. */
    MOVED
  }

  /** the definition panel that was added/removed. */
  protected SortDefinitionPanel m_SortDefinitionPanel;

  /** what type of event occurred. */
  protected EventType m_Type;

  /**
   * Initializes the event.
   *
   * @param source	the {@link SortPanel} that triggered the event
   * @param definition	the definition panel that was added or removed, null if reset
   * @param type	the type of event
   */
  public SortSetupEvent(SortPanel source, SortDefinitionPanel definition, EventType type) {
    super(source);
    m_SortDefinitionPanel = definition;
    m_Type                = type;
  }

  /**
   * Returns the {@link SortPanel} that triggered the event
   *
   * @return		 the panel
   */
  public SortPanel getSortPanel() {
    return (SortPanel) getSource();
  }

  /**
   * Returns the {@link SortDefinitionPanel} that was added/removed.
   *
   * @return		the panel, null in case of reset
   */
  public SortDefinitionPanel getSortDefinitionPanel() {
    return m_SortDefinitionPanel;
  }

  /**
   * Returns the event type.
   *
   * @return		true if the panel was added
   */
  public EventType getType() {
    return m_Type;
  }

  /**
   * Returns a short description of the object.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "sortpanel=" + getSortPanel().hashCode() + ", sortdefinitionpanel=[" + getSortDefinitionPanel() + "], type=" + m_Type;
  }
}
