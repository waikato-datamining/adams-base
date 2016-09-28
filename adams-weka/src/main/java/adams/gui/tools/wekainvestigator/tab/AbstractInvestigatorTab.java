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
 * AbstractInvestigatorTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.CleanUpHandler;
import adams.core.StatusMessageHandler;
import adams.gui.core.DetachablePanel;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.event.WekaInvestigatorDataListener;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;

import java.util.List;

/**
 * Ancestor for tabs in the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInvestigatorTab
  extends DetachablePanel
  implements WekaInvestigatorDataListener, StatusMessageHandler, CleanUpHandler {

  private static final long serialVersionUID = 1860821657853747908L;

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  public void setOwner(InvestigatorPanel value) {
    m_Owner = value;
    dataChanged(new WekaInvestigatorDataEvent(m_Owner));
  }

  /**
   * Returns the owner of this tab.
   *
   * @return		the owner, null if none set
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Returns the icon name for the tab icon.
   * <br>
   * Default implementation returns null.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return null;
  }

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return getOwner().getData();
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public abstract void dataChanged(WekaInvestigatorDataEvent e);

  /**
   * Notifies all the tabs that the data has changed.
   *
   * @param e		the event to send
   */
  public void fireDataChange(WekaInvestigatorDataEvent e) {
    getOwner().fireDataChange(e);
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    if (!msg.isEmpty())
      getOwner().logMessage("[" + getTitle() + "] " + msg);
  }

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param msg		the log message
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(String msg, Throwable t, String title) {
    getOwner().logError("[" + getTitle() + "] " + msg, t, title);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    getOwner().logError("[" + getTitle() + "] " + msg, title);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    logMessage(msg);
    getOwner().showStatus("[" + getTitle() + "] " + msg);
  }
}
