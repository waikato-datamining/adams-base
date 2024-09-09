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
 * AbstractEditableDataTableAction.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.datatable.action;

import adams.core.ClassLister;
import adams.core.StatusMessageHandler;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.DataContainerList;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTabWithEditableDataTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for actions on the data displayed on a tab using a
 * {@link AbstractInvestigatorTabWithEditableDataTable}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractEditableDataTableAction
  extends AbstractBaseAction
  implements StatusMessageHandler {

  private static final long serialVersionUID = -3555111594280198534L;

  /** the owner. */
  protected AbstractInvestigatorTabWithEditableDataTable m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractInvestigatorTabWithEditableDataTable value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner, null if none set
   */
  public AbstractInvestigatorTabWithEditableDataTable getOwner() {
    return m_Owner;
  }

  /**
   * Returns the table.
   *
   * @return    the table
   */
  public BaseTableWithButtons getTable() {
    return m_Owner.getTable();
  }

  /**
   * Returns the currently selected data containers.
   *
   * @return		the selected data
   */
  protected int[] getSelectedRows() {
    return getTable().getSelectedRows();
  }

  /**
   * Returns the currently selected data containers.
   *
   * @return		the selected data
   */
  protected DataContainer[] getSelectedData() {
    List<DataContainer> result;
    int[]		rows;
    int			i;

    rows = getSelectedRows();
    result = new ArrayList<>();
    for (i = 0; i < rows.length; i++) {
      if (rows[i] < getData().size())
	result.add(getData().get(rows[i]));
    }

    return result.toArray(new DataContainer[0]);
  }

  /**
   * Updates the action.
   */
  public abstract void update();

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public DataContainerList getData() {
    return getOwner().getData();
  }

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
    getOwner().logMessage(msg);
  }

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param msg		the log message
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(String msg, Throwable t, String title) {
    getOwner().logError(msg, t, title);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    getOwner().logError(msg, title);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    if (m_Owner == null)
      return;
    m_Owner.showStatus(msg);
  }

  /**
   * Returns whether the tab is busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return m_Owner.isBusy();
  }

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getActions() {
    return ClassLister.getSingleton().getClasses(AbstractEditableDataTableAction.class);
  }
}
