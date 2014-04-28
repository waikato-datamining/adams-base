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
 * ReportContainerManager.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

import adams.data.report.Report;
import adams.db.AbstractDatabaseConnection;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DatabaseContainerManager;
import adams.gui.visualization.container.NamedContainerManager;

/**
 * A container manager for Report objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportContainerManager
  extends AbstractContainerManager<ReportContainer>
  implements /* VisibilityContainerManager, */
             NamedContainerManager, DatabaseContainerManager<ReportContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -6144975286254812052L;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Initializes the manager.
   *
   * @param dbcon	the database context
   */
  public ReportContainerManager(AbstractDatabaseConnection dbcon) {
    super();

    m_DatabaseConnection = dbcon;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection = value;
  }

  /**
   * Returns the indices of all visible containers.
   *
   * @return		all containers
   */
  public int[] getVisibleIndices() {
    TIntArrayList	result;
    int			i;

    result = new TIntArrayList();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      result.add(i);
    }

    return result.toArray();
  }

  /**
   * Returns (a copy of) all currently stored containers. Those containers
   * have no manager.
   *
   * @return		all containers
   */
  public List<ReportContainer> getAllVisible() {
    List<ReportContainer>	result;
    ReportContainer		cont;
    int				i;

    result = new ArrayList<ReportContainer>();

    for (i = 0; i < count(); i++) {
      if (!isVisible(i))
        continue;
      cont = (ReportContainer) get(i).copy();
      cont.setManager(null);
      result.add(cont);
    }

    return result;
  }

  /**
   * Returns whether the container at the specified position is visible.
   *
   * @param index	the container's position
   * @return		true if the container is visible
   */
  public boolean isVisible(int index) {
    return get(index).isVisible();
  }

  /**
   * Sets the specified container's visibility. Uses the scripting engine
   * if the owner is derived from SpectrumPanel.
   *
   * @param index	the index of the container
   * @param visible	if true then the container will be made visible
   * @see		SequencePanel
   */
  public void setVisible(int index, boolean visible) {
    get(index).setVisible(visible);

    notifyDataChangeListeners(new DataChangeEvent(this, Type.VISIBILITY, index));
  }

  /**
   * Returns the nth visible container.
   *
   * @param index	the index (relates only to the visible containers!)
   * @return		the container, null if index out of range
   */
  public ReportContainer getVisible(int index) {
    ReportContainer	result;
    int			i;
    int			count;

    result = null;
    count  = -1;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
	count++;
      if (count == index) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the number of visible containers.
   *
   * @return		the number of visible containers
   */
  public int countVisible() {
    int	result;
    int	i;

    result = 0;

    for (i = 0; i < count(); i++) {
      if (isVisible(i))
        result++;
    }

    return result;
  }

  /**
   * Determines the index of the reports with the specified ID.
   *
   * @param id		the ID of the report
   * @return		the index of the report or -1 if not found
   */
  public int indexOf(String id) {
    int	result;
    int	i;

    result = -1;

    for (i = 0; i < count(); i++) {
      if (get(i).getID().equals(id)) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  @Override
  public ReportContainer newContainer(Comparable o) {
    return new ReportContainer(this, (Report) o);
  }
}
