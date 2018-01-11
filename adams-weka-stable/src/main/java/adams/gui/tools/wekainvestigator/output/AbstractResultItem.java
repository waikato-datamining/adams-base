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
 * AbstractResultItem.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.gui.core.SimpleLogPanel;
import weka.core.Instances;

import java.util.Date;

/**
 * Container for a data to be stored in result history.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractResultItem
  extends LoggingObject
  implements CleanUpHandler {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the maximum length for the relationname when assembling the name. */
  public final static int MAX_RELATIONNAME_LENGTH = 30;

  /** the timestamp. */
  protected Date m_Timestamp;

  /** the name of the item. */
  protected String m_Name;

  /** the header. */
  protected Instances m_Header;

  /** the log. */
  protected StringBuilder m_Log;

  /** the log component. */
  protected SimpleLogPanel m_LogPanel;

  /** the tabbed pane with the generated output. */
  protected transient OutputTabbedPane m_TabbedPane;

  /**
   * Initializes the item.
   *
   * @param header	the header of the training set, can be null
   */
  protected AbstractResultItem(Instances header) {
    m_Header    = header;
    m_Timestamp = new Date();
    m_Name      = null;
    m_Log       = new StringBuilder();
    m_LogPanel  = new SimpleLogPanel();
  }

  /**
   * Creates the name from the members.
   *
   * @return		the name
   */
  protected abstract String createName();

  /**
   * Returns the name of the item.
   *
   * @return		the name
   */
  public synchronized String getName() {
    if (m_Name == null)
      m_Name = createName();
    return m_Name;
  }

  /**
   * Returns whether an training set header is present.
   * 
   * @return		true if available
   */
  public boolean hasHeader() {
    return (m_Header != null);
  }

  /**
   * Returns the stored training set header.
   * 
   * @return		the header, null if not present
   */
  public Instances getHeader() {
    return m_Header;
  }

  /**
   * The tabbed pane for the results.
   *
   * @return		the tabbed pane
   */
  public synchronized OutputTabbedPane getTabbedPane() {
    if (m_TabbedPane == null) {
      m_TabbedPane = new OutputTabbedPane();
      m_TabbedPane.setShowCloseTabButton(true);
      m_TabbedPane.setCloseTabsWithMiddleMouseButton(false);
    }
    return m_TabbedPane;
  }

  /**
   * Returns the log panel.
   *
   * @return		the log panel
   */
  public SimpleLogPanel getLogPanel() {
    return m_LogPanel;
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    if (!msg.isEmpty()) {
      m_Log.append(msg);
      m_Log.append("\n");
      m_LogPanel.append(LoggingLevel.FINE, msg);
    }
  }

  /**
   * Logs the exception.
   *
   * @param msg		the log message
   * @param t		the exception
   */
  public void logError(String msg, Throwable t) {
    String	exc;

    exc = Utils.throwableToString(t);

    m_Log.append(msg);
    m_Log.append("\n");
    m_LogPanel.append(LoggingLevel.SEVERE, msg);

    m_Log.append(exc);
    m_Log.append("\n");
    m_LogPanel.append(LoggingLevel.SEVERE, exc);
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  public abstract String toString();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_TabbedPane != null) {
      m_TabbedPane.cleanUp();
      m_TabbedPane = null;
    }
  }
}
