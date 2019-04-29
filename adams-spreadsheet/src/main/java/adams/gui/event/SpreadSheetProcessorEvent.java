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
 * SpreadSheetProcessorEvent.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.tools.spreadsheetprocessor.SpreadSheetProcessorPanel;
import adams.gui.tools.sqlworkbench.SqlQueryPanel;

import java.util.EventObject;

/**
 * Events that get sent when changes occur in the {@link SpreadSheetProcessorPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetProcessorEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -1322917092297786611L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum EventType {
    /** data has become available. */
    DATA_IS_AVAILABLE,
    /** data to be processed. */
    PROCESS_DATA,
    /** data has been processed. */
    DATA_IS_PROCESSED,
    /** data to be output. */
    OUTPUT_DATA,
    /** data has been output. */
    DATA_IS_OUTPUT,
  }

  /** what type of event occurred. */
  protected EventType m_Type;

  /** optional message. */
  protected String m_Message;

  /**
   * Initializes the event.
   *
   * @param source	the {@link SqlQueryPanel} that triggered the event
   * @param type	the type of event
   */
  public SpreadSheetProcessorEvent(SpreadSheetProcessorPanel source, EventType type) {
    this(source, type, null);
  }

  /**
   * Initializes the event.
   *
   * @param source	the {@link SqlQueryPanel} that triggered the event
   * @param type	the type of event
   * @param message 	the optional message, can be null
   */
  public SpreadSheetProcessorEvent(SpreadSheetProcessorPanel source, EventType type, String message) {
    super(source);
    m_Type    = type;
    m_Message = message;
  }

  /**
   * Returns the {@link SpreadSheetProcessorPanel} that triggered the event
   *
   * @return		 the panel
   */
  public SpreadSheetProcessorPanel getSpreadSheetProcessorPanel() {
    return (SpreadSheetProcessorPanel) getSource();
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
   * Returns the optional message.
   *
   * @return		the message, null if none available
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns a short description of the object.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "panel=" + getSpreadSheetProcessorPanel().hashCode() + ", type=" + m_Type + ", msg=" + m_Message;
  }
}
