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
 * SqlQueryPanelEvent.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.tools.sqlworkbench.SqlQueryPanel;

import java.util.EventObject;

/**
 * Event that gets sent when {@link SqlQueryPanelEvent} changes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SqlQueryPanelEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -1322917092297786611L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum EventType {
    /** the query was modified. */
    QUERY_CHANGED,
    /** a query was run successfully (but no output). */
    QUERY_SUCCESS_NOOUTPUT,
    /** a query was run successfully (with output). */
    QUERY_SUCCESS_WITHOUTPUT,
    /** a query generated an error. */
    QUERY_ERROR,
  }

  /** what type of event occurred. */
  protected EventType m_Type;

  /**
   * Initializes the event.
   *
   * @param source	the {@link SqlQueryPanel} that triggered the event
   * @param type	the type of event
   */
  public SqlQueryPanelEvent(SqlQueryPanel source, EventType type) {
    super(source);
    m_Type = type;
  }

  /**
   * Returns the {@link SqlQueryPanel} that triggered the event
   *
   * @return		 the panel
   */
  public SqlQueryPanel getSqlQueryPanel() {
    return (SqlQueryPanel) getSource();
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
    return "sqlquerypanel=" + getSqlQueryPanel().hashCode() + ", type=" + m_Type;
  }
}
